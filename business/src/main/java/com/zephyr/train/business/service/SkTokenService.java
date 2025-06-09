package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.business.domain.SkToken;
import com.zephyr.train.business.domain.SkTokenExample;
import com.zephyr.train.business.enums.RedisKeyPreEnum;
import com.zephyr.train.business.mapper.SkTokenMapper;
import com.zephyr.train.business.mapper.cust.SkTokenMapperCust;
import com.zephyr.train.business.req.SkTokenQueryReq;
import com.zephyr.train.business.req.SkTokenSaveReq;
import com.zephyr.train.business.resp.SkTokenQueryResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SkTokenService {

  private static final Logger LOG = LoggerFactory.getLogger(SkTokenService.class);

  @Resource
  private SkTokenMapper skTokenMapper;

  @Resource
  private DailyTrainSeatService dailyTrainSeatService;

  @Resource
  private DailyTrainStationService dailyTrainStationService;

  @Resource
  private SkTokenMapperCust skTokenMapperCust;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Value("${spring.profiles.active}")
  private String env;

  /**
   * Initialization
   */
  public void genDaily(Date date, String trainCode) {
    LOG.info("Delete token records of train[{}] for date[{}]", trainCode, DateUtil.formatDate(date));
    SkTokenExample skTokenExample = new SkTokenExample();
    skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
    skTokenMapper.deleteByExample(skTokenExample);

    DateTime now = DateTime.now();
    SkToken skToken = new SkToken();
    skToken.setDate(date);
    skToken.setTrainCode(trainCode);
    skToken.setId(SnowUtil.getSnowflakeNextId());
    skToken.setCreateTime(now);
    skToken.setUpdateTime(now);

    int seatCount = dailyTrainSeatService.countSeat(date, trainCode);
    LOG.info("Seat number of train[{}]: {}", trainCode, seatCount);

    long stationCount = dailyTrainStationService.countByTrainCode(date, trainCode);
    LOG.info("Station number of train[{}]: {}", trainCode, stationCount);

    // The ratio value should be set according to the actual ticket‐selling case; a single train can sell at most seatCount * (stationCount – 1) tickets.
    // count = (seatCount * (stationCount - 1) * ratio);
    double ratio = 1.0;
    int count = (int) (seatCount * (stationCount - 1) * ratio);
    LOG.info("Initialized token number of train[{}]: {}", trainCode, count);
    skToken.setCount(count);

    skTokenMapper.insert(skToken);
  }

  public void save(SkTokenSaveReq req) {
    DateTime now = DateTime.now();
    SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
    if (ObjectUtil.isNull(skToken.getId())) {
      skToken.setId(SnowUtil.getSnowflakeNextId());
      skToken.setCreateTime(now);
      skToken.setUpdateTime(now);
      skTokenMapper.insert(skToken);
    } else {
      skToken.setUpdateTime(now);
      skTokenMapper.updateByPrimaryKey(skToken);
    }
  }

  public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req) {
    SkTokenExample skTokenExample = new SkTokenExample();
    skTokenExample.setOrderByClause("id desc");
    SkTokenExample.Criteria criteria = skTokenExample.createCriteria();

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);

    PageInfo<SkToken> pageInfo = new PageInfo<>(skTokenList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<SkTokenQueryResp> list = BeanUtil.copyToList(skTokenList, SkTokenQueryResp.class);
    PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    skTokenMapper.deleteByPrimaryKey(id);
  }

  /**
   * Validate token
   */
  public boolean validSkToken(Date date, String trainCode, Long memberId) {
    LOG.info("Member[{}] starts to get the token of train[{}] for date[{}]", memberId, trainCode, DateUtil.formatDate(date));

    if (!env.equals("dev")) {
      // First acquire the token lock, then verify the remaining token count to prevent bots from snatching tickets. The 'lockKey' itself serves as the token — a credential that indicates who is allowed to do what.
      String lockKey = RedisKeyPreEnum.SK_TOKEN + "-" + DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
      Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
      if (Boolean.TRUE.equals(setIfAbsent)) {
        LOG.info("Congratulation, got the token lock! lockKey：{}", lockKey);
      } else {
        LOG.info("Unfortunately, failed to acquire the token lock! lockKey：{}", lockKey);
        return false;
      }
    }

    // redis cache + database
    String skTokenCountKey = RedisKeyPreEnum.SK_TOKEN_COUNT + "-" + DateUtil.formatDate(date) + "-" + trainCode;
    Object skTokenCount = redisTemplate.opsForValue().get(skTokenCountKey);
    if (skTokenCount != null) {
      LOG.info("The cache key for this train’s token gate: {}", skTokenCountKey);
      Long count = redisTemplate.opsForValue().decrement(skTokenCountKey, 1);
      if (count < 0L) {
        LOG.error("Fail to get token: {}", skTokenCountKey);
        return false;
      } else {
        LOG.info("After getting token, remaining token number: {}", count);
        redisTemplate.expire(skTokenCountKey, 60, TimeUnit.SECONDS);
        // Update database after getting 5 tokens
        if (count % 5 == 0) {
          skTokenMapperCust.set(date, trainCode, count.intValue());
        }
        return true;
      }
    } else {
      LOG.info("No cache key for this train’s token gate: {}", skTokenCountKey);
      // Check if there are tokens remaining
      SkTokenExample skTokenExample = new SkTokenExample();
      skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
      List<SkToken> tokenCountList = skTokenMapper.selectByExample(skTokenExample);
      if (CollUtil.isEmpty(tokenCountList)) {
        LOG.info("Fail to find token record of train[{}] for date[{}]", trainCode, DateUtil.formatDate(date));
        return false;
      }

      SkToken skToken = tokenCountList.get(0);
      if (skToken.getCount() <= 0) {
        LOG.info("Token of train[{}] for date[{}] is 0",  trainCode, DateUtil.formatDate(date));
        return false;
      }

      // There are tokens remaining
      // token - 1
      Integer count = skToken.getCount() - 1;
      skToken.setCount(count);
      LOG.info("Store token gate of this train in cache, key: {}, count: {}", skTokenCountKey, count);
      // No need to update database, just modify cache
      redisTemplate.opsForValue().set(skTokenCountKey, String.valueOf(count), 60, TimeUnit.SECONDS);
//      skTokenMapper.updateByPrimaryKey(skToken);
      return true;
    }

//    // Tokens roughly represent the inventory. Once the tokens are exhausted, ticket sales stop and there is no need to enter the main purchase flow to check stock, since checking tokens is definitely more efficient than checking inventory.
//    int updateCount = skTokenMapperCust.decrease(date, trainCode, 1);
//    if (updateCount > 0) {
//      return true;
//    } else {
//      return false;
//    }
  }
}
