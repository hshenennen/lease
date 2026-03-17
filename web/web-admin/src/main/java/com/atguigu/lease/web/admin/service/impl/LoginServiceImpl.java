package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.web.admin.service.LoginService;
import com.atguigu.lease.web.admin.vo.login.CaptchaVo;
import com.wf.captcha.SpecCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;//Redis

	//获取图形验证码
	@Override
	public CaptchaVo getCaptcha() {
		// 创建验证码对象：宽130像素，高48像素，4位字符
		SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);
		//获取验证码文本并转换为小写
		String code = specCaptcha.text().toLowerCase();
		// 生成唯一标识 key
		String key = RedisConstant.ADMIN_LOGIN_PREFIX + UUID.randomUUID();
		// 将验证码存入 Redis，60秒过期
		stringRedisTemplate.opsForValue().set(key, code, RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);
		return new CaptchaVo(specCaptcha.toBase64(), key);
	}
}
