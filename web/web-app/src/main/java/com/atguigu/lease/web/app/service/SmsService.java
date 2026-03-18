package com.atguigu.lease.web.app.service;

public interface SmsService {

	//发送短信
	void sendCode(String phone,String code);
}
