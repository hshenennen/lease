package com.atguigu.lease.web.app.service;

import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;

public interface LoginService {
	//获取短信验证码
	void getCode(String phone);

	//登录
	String login(LoginVo loginVo);

	//获取登录用户信息
	UserInfoVo getUserInfoVo(Long userId);
}
