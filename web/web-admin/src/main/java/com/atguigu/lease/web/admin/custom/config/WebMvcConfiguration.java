package com.atguigu.lease.web.admin.custom.config;

import com.atguigu.lease.web.admin.custom.converter.StringToBaseEnumConverterFactory;
import com.atguigu.lease.web.admin.custom.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//在MCV中声明我们的枚举类型转换
public class WebMvcConfiguration implements WebMvcConfigurer {

	//@Autowired
	//private StringToItemTypeConverter stringToItemTypeConverter;

	@Autowired
	private StringToBaseEnumConverterFactory stringToBaseEnumConverterFactory;

	@Autowired
	private AuthenticationInterceptor authenticationInterceptor;

	@Override
	public void addFormatters(FormatterRegistry registry) {
		//registry.addConverter(this.stringToItemTypeConverter);
		registry.addConverterFactory(this.stringToBaseEnumConverterFactory);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 将身份验证拦截器添加到Spring MVC拦截器链中
		registry.addInterceptor(this.authenticationInterceptor)
				.addPathPatterns("/admin/**")// 设置需要拦截的路径模式
				.excludePathPatterns("/admin/login/**"); // 设置需要排除的路径模式
	}
}
