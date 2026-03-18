package com.atguigu.lease.web.app.service;

public interface LoginService {
	//获取短信验证码
	void getCode(String phone);
}
