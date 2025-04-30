package com.zephyr.train.member.service;

import cn.hutool.core.collection.CollUtil;
import com.zephyr.train.member.domain.Member;
import com.zephyr.train.member.domain.MemberExample;
import com.zephyr.train.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

  @Resource
  private MemberMapper memberMapper;

  public int count() {
    return Math.toIntExact(memberMapper.countByExample(null));
  }

  public long register(String mobile) {
    MemberExample memberExample = new MemberExample();
    memberExample.createCriteria().andMobileEqualTo(mobile);
    List<Member> list = memberMapper.selectByExample(memberExample);

    if (CollUtil.isNotEmpty(list)) {
//      return list.get(0).getId();
      throw new RuntimeException("mobile has been registered");
    }
    Member member = new Member();
    member.setId(System.currentTimeMillis());
    member.setMobile(mobile);
    memberMapper.insert(member);
    return member.getId();
  }
}
