package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.admin.mapper.SystemUserMapper;
import com.atguigu.lease.web.admin.service.LoginService;
import com.atguigu.lease.web.admin.vo.login.CaptchaVo;
import com.atguigu.lease.web.admin.vo.login.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wf.captcha.SpecCaptcha;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;//Redis

	@Autowired
	private SystemUserMapper systemUserMapper;//员工信息

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

	@Override
	public String login(LoginVo loginVo) {
		//判断captchaCode是否为空，若为空，则直接响应验证码为空
		if (loginVo.getCaptchaCode() == null) {
			throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_NOT_FOUND);
		}
		String code = stringRedisTemplate.opsForValue().get(loginVo.getCaptchaKey());//验证码
		//根据captchaKey从Redis中查询之前保存的code,若查询出来的code为空，则直接响应验证码已过期。
		if (code == null) {
			throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_EXPIRED);
		}
		//比较captchaCode和code，若不相同，则直接响应验证码不正确
		if (!code.equals(loginVo.getCaptchaCode().toLowerCase())) {
			throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_ERROR);
		}
		//根据username查询数据库，若查询结果为空，则直接响应账号不存在
		SystemUser systemUser = systemUserMapper.getSystemUserByUserName(loginVo.getUsername());
		if (systemUser.getUsername() == null) {
			throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
		}
		//查看用户状态判断是否被禁用，若禁用，则直接响应账号被禁
		if (systemUser.getStatus() == BaseStatus.DISABLE) {
			throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);
		}
		//比对password和数据库中查询的密码，若不一致，则直接响应账号或密码错误
		if (!systemUser.getPassword().equals(DigestUtils.md5Hex(loginVo.getPassword()))) {
			throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
		}

		//创建并返回token
		return JwtUtil.createToken(systemUser.getId(), systemUser.getUsername());
	}
}
