package com.zephyr.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zephyr.train.common.exception.BusinessException;
import com.zephyr.train.common.exception.BusinessExceptionEnum;
import com.zephyr.train.common.util.JwtUtil;
import com.zephyr.train.common.util.SnowUtil;
import com.zephyr.train.member.domain.Member;
import com.zephyr.train.member.domain.MemberExample;
import com.zephyr.train.member.mapper.MemberMapper;
import com.zephyr.train.member.req.MemberLoginReq;
import com.zephyr.train.member.req.MemberRegisterReq;
import com.zephyr.train.member.req.MemberSendCodeReq;
import com.zephyr.train.member.resp.MemberLoginResp;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

  private static final Logger LOG = LoggerFactory.getLogger(MemberService.class);

  @Resource
  private MemberMapper memberMapper;

  public int count() {
    return Math.toIntExact(memberMapper.countByExample(null));
  }

  public long register(MemberRegisterReq req) {
    String mobile = normalizePhone(req.getMobile());
    Member memberDB = selectByMobile(mobile);

    if (ObjectUtil.isNotNull(memberDB)) {
//      return list.get(0).getId();
      throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_REGISTERED);
    }
    Member member = new Member();
    member.setId(SnowUtil.getSnowflakeNextId());
    member.setMobile(mobile);
    memberMapper.insert(member);
    return member.getId();
  }

  public void sendCode(MemberSendCodeReq req) {
    String mobile = normalizePhone(req.getMobile());
    Member memberDB = selectByMobile(mobile);

    // If mobile has not been registered, sign up
    if (ObjectUtil.isNull(memberDB)) {
      LOG.info("Mobile not exist, insert a record");
      Member member = new Member();
      member.setId(SnowUtil.getSnowflakeNextId());
      member.setMobile(mobile);
      memberMapper.insert(member);
    } else {
      LOG.info("Mobile exists, do not insert a record");
    }

    String code = "123456";

    // TO-DO Generate verification code: for demo, code is kept as '123456'. In real project, need to implement the following part
    // String code = RandomUtil.randomString(6);

    LOG.info("Generate verification code: {}", code);

    // SMS record table: mobile, SMS code, expire time, isUsed, business type, send time, used time
    LOG.info("Record SMS code to table");

    // SMS channel to send code
    LOG.info("SMS channel to send code");

  }

  public MemberLoginResp login(MemberLoginReq req) {
    String mobile = normalizePhone(req.getMobile());
    String code = req.getCode();
    Member memberDB = selectByMobile(mobile);

    // If mobile has not been registered, sign up first
    if (ObjectUtil.isNull(memberDB)) {
      throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_REGISTERED);
    }

    // Check SMS code
    // TO-DO Generate verification code
    if (!"123456".equals(code)) {
      throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
    }

    MemberLoginResp memberLoginResp = BeanUtil.copyProperties(memberDB, MemberLoginResp.class);
    String token = JwtUtil.createToken(memberLoginResp.getId(), memberLoginResp.getMobile());
    memberLoginResp.setToken(token);
    return memberLoginResp;
  }

  private Member selectByMobile(String mobile) {
    MemberExample memberExample = new MemberExample();
    memberExample.createCriteria().andMobileEqualTo(mobile);
    List<Member> list = memberMapper.selectByExample(memberExample);
    if (CollUtil.isEmpty(list)) {
      return null;
    } else {
      return list.get(0);
    }
  }


  private String normalizePhone(String mobile) {
    // Remove all non-digit characters
    String digits = mobile.replaceAll("\\D", "");

    // Remove leading '1' if it's a country code and length is 11
    if (digits.length() == 11 && digits.startsWith("1")) {
      digits = digits.substring(1);
    }

    // Optionally validate it's now exactly 10 digits, but this case has already been avoided in MemberSendCodeReq
    if (digits.length() != 10) {
      return "Invalid number";
    }

    if (digits.length() >= 7) {
      return digits.substring(0, 3) + "-" +
          digits.substring(3, 6) + "-" +
          digits.substring(6);
    } else if (digits.length() >= 4) {
      return digits.substring(0, 3) + "-" +
          digits.substring(3);
    } else {
      return digits;
    }
  }
}
