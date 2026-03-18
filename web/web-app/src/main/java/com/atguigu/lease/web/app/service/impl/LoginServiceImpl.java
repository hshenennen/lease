package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.web.app.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

//获取短信验证码
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;//Redis

	//获取短信验证码
	@Override
	public void getCode(String phone) {
		String key = RedisConstant.ADMIN_LOGIN_PREFIX + phone;

		//生成验证码
		SecureRandom random = new SecureRandom();
		int fourDigit = 1000 + random.nextInt(9000); // 1000-9999
		String code = String.valueOf(fourDigit);
		System.out.println("验证码："+code);

		// 将验证码存入 Redis，60秒过期
		stringRedisTemplate.opsForValue().set(key, code, RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);
	}
}
