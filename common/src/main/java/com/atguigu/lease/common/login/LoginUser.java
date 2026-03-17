package com.atguigu.lease.common.login;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
/**
 * 登录用户信息实体类
 * 使用Lombok注解简化代码
 * @Data 生成getter、setter、toString、equals、hashCode方法
 * @AllArgsConstructor 生成全参数构造函数
 */
public class LoginUser {

	private Long userId;//用户ID
	private String username;//用户名
}
