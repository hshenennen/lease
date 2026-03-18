package com.atguigu.lease.web.app.custom.config;

import com.atguigu.lease.web.app.custom.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * Web MVC配置类
 * 实现WebMvcConfigurer接口，用于配置Spring MVC的拦截器
 * 主要功能：注册自定义的身份验证拦截器，并配置拦截路径规则
 */
@Configuration // 标识这是一个Spring配置类，Spring容器启动时会自动处理
public class WebMvcConfiguration implements WebMvcConfigurer {
	/**
	 * 注入自定义的身份验证拦截器
	 * Spring会自动装配AuthenticationInterceptor实例
	 */
	@Autowired
	private AuthenticationInterceptor authenticationInterceptor;//拦截器实例

	/**
	 * 添加拦截器配置
	 * 重写WebMvcConfigurer接口的方法，配置自定义拦截器的路径规则
	 *
	 * @param registry 拦截器注册表，用于注册和管理拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册身份验证拦截器，并配置路径匹配规则
		registry.addInterceptor(this.authenticationInterceptor)
				.addPathPatterns("/app/**") //设置需要拦截的路径模式：所有以/app/开头的请求
				.excludePathPatterns("/app/login/**"); //设置排除拦截的路径模式：所有以/app/login/开头的请求
	}
}