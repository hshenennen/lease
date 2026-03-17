package com.atguigu.lease.web.admin.service;

import com.atguigu.lease.web.admin.vo.login.CaptchaVo;
import com.atguigu.lease.web.admin.vo.login.LoginVo;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserInfoVo;

public interface LoginService {

	//获取图形验证码
	CaptchaVo getCaptcha();

	//登录
	String login(LoginVo loginVo);

	//获取登陆用户个人信息
	SystemUserInfoVo getSystemUserInfoVoById(Long userId);
}
