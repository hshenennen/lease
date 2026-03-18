package com.atguigu.lease.web.app.custom.interceptor;

import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
/**
 * 身份验证拦截器
 * 实现HandlerInterceptor接口，用于在请求处理前后进行身份验证和用户信息管理
 * 主要功能：解析JWT token获取用户信息，并通过ThreadLocal存储用户信息供后续使用
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
	/**
	 * 请求处理前执行的方法
	 * 从请求头中获取access-token，解析JWT token获取用户信息，并存入LoginUserHolder
	 *
	 * @param request HttpServletRequest对象
	 * @param response HttpServletResponse对象
	 * @param handler 目标处理器
	 * @return boolean 是否继续执行后续处理链
	 *          true: 继续执行后续拦截器和控制器
	 *          false: 中断请求处理
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 从请求头中获取JWT token
		String token = request.getHeader("access-token");
		// 解析JWT token，获取其中存储的声明信息
		Claims claims = JwtUtil.parseToken(token);
		// 从claims中提取用户ID和用户名
		Long userId = claims.get("userId", Long.class);
		String username = claims.get("username", String.class);

		// 将用户信息存入ThreadLocal，供当前线程后续使用
		LoginUserHolder.setLoginUser(new LoginUser(userId, username));

		return true;
	}

	/**
	 * 请求完成后执行的方法（在视图渲染之后）
	 * 清理ThreadLocal中存储的用户信息，防止内存泄漏
	 *
	 * @param request HttpServletRequest对象
	 * @param response HttpServletResponse对象
	 * @param handler 目标处理器
	 * @param ex 处理过程中抛出的异常（如果有）
	 * @throws Exception 可能抛出的异常
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		// 清除ThreadLocal中的用户信息，确保线程复用时不会获取到错误的用户信息
		LoginUserHolder.clear();
	}
}
