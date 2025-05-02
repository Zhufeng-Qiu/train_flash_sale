package com.zephyr.train.gateway.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtUtil {
  private static final Logger LOG = LoggerFactory.getLogger(JwtUtil.class);

  /**
   * Secret key is important, must not be leaked and should be different for each project, can be set in config file
   */
  private static final String key = "zephyr123456";

  public static String createToken(Long id, String mobile) {
    DateTime now = DateTime.now();
    DateTime expTime = now.offsetNew(DateField.SECOND, 10);
    Map<String, Object> payload = new HashMap<>();
    // Issued time
    payload.put(JWTPayload.ISSUED_AT, now);
    // Expire time
    payload.put(JWTPayload.EXPIRES_AT, expTime);
    // Effective time
    payload.put(JWTPayload.NOT_BEFORE, now);
    // content
    payload.put("id", id);
    payload.put("mobile", mobile);
    String token = JWTUtil.createToken(payload, key.getBytes());
    LOG.info("Generate JWT token：{}", token);
    return token;
  }

  public static boolean validate(String token) {
    try {
      JWT jwt = JWTUtil.parseToken(token).setKey(key.getBytes());
      // validate包含了verify
      boolean validate = jwt.validate(0);
      LOG.info("JWT token verification result：{}", validate);
      return validate;
    } catch (Exception e) {
      LOG.error("JWT token verification error", e);
      return false;
    }
  }

  public static JSONObject getJSONObject(String token) {
    JWT jwt = JWTUtil.parseToken(token).setKey(key.getBytes());
    JSONObject payloads = jwt.getPayloads();
    payloads.remove(JWTPayload.ISSUED_AT);
    payloads.remove(JWTPayload.EXPIRES_AT);
    payloads.remove(JWTPayload.NOT_BEFORE);
    LOG.info("Extract origin content based on token：{}", payloads);
    return payloads;
  }

  public static void main(String[] args) {
    createToken(1L, "123");

    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3NDYxNzI2NzQsIm1vYmlsZSI6IjEyMyIsImlkIjoxLCJleHAiOjE3NDYxNzI2ODQsImlhdCI6MTc0NjE3MjY3NH0.HjeDKI3y8mzTi96Yj32AlXriDe0lqTIbYGTq4bHZo1w";
    validate(token);

    getJSONObject(token);
  }
}