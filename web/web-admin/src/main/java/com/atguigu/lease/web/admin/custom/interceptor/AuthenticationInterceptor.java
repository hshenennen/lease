package com.atguigu.lease.web.admin.custom.interceptor;

import com.atguigu.lease.common.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component // 注册为Spring组件，自动被Spring容器管理
public class AuthenticationInterceptor implements HandlerInterceptor {
	/**
	 * 在Controller方法执行前调用
	 *
	 * @param request HTTP请求对象
	 * @param response HTTP响应对象
	 * @param handler 将要执行的处理程序（Controller方法）
	 * @return true表示继续执行，false表示中断请求
	 * @throws Exception 可能抛出的异常
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 1. 从请求头获取JWT令牌
		// 约定令牌放在"access-token"请求头中
		String token = request.getHeader("access-token");

		// 2. 验证令牌
		// 这里只是简单地调用解析方法，如果令牌无效会抛出异常
		JwtUtil.parseToken(token);

		// 3. 验证通过，继续执行后续拦截器和Controller方法
		return true;
	}
}
