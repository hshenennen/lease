package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.app.service.LoginService;
import com.atguigu.lease.web.app.service.UserInfoService;
import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

	@Autowired
	private UserInfoService userInfoService;//用户信息

	//获取短信验证码
	@Override
	public void getCode(String phone) {

		String key = RedisConstant.ADMIN_LOGIN_PREFIX + phone;

		//生成验证码
		SecureRandom random = new SecureRandom();
		int fourDigit = 1000 + random.nextInt(9000); // 1000-9999
		String code = String.valueOf(fourDigit);//int转换String
		System.out.println("验证码：" + code);

		// 将验证码存入 Redis，60秒过期
		stringRedisTemplate.opsForValue().set(key, code, RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);
	}

	//登录
	@Override
	public String login(LoginVo loginVo) {
		//1.判断手机号码和验证码是否为空
		if (loginVo.getPhone() == null) {
			throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);
		}
		if (loginVo.getCode() == null) {
			throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);
		}
		//2.校验验证码
		String key = RedisConstant.ADMIN_LOGIN_PREFIX + loginVo.getPhone();
		String code = stringRedisTemplate.opsForValue().get(key);
		if (code == null) {
			throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EXPIRED);
		}
		if (!code.equals(loginVo.getCode())) {
			throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);
		}
		//3.判断用户是否存在,不存在则注册（创建用户）
		LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<UserInfo>()
				.eq(UserInfo::getPhone, loginVo.getPhone());
		UserInfo userInfo = userInfoService.getOne(userInfoLambdaQueryWrapper);
		if (userInfo == null) {
			//创建用户
			UserInfo userInfo1 = new UserInfo();
			userInfo1.setPhone(loginVo.getPhone());
			userInfo1.setStatus(BaseStatus.ENABLE);
			userInfo1.setNickname("用户-" + loginVo.getPhone().substring(6));
			//保存到数据库
			userInfoService.save(userInfo1);
		}
		//4.判断用户是否被禁
		if (userInfo.getStatus().equals(BaseStatus.DISABLE)) {
			throw new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
		}

		//5.创建并返回token
		return JwtUtil.createToken(userInfo.getId(), userInfo.getPhone());
	}
}
