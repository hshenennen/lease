package com.atguigu.lease.web.admin.custom.interceptor;

import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component // 注册为Spring组件，自动被Spring容器管理
public class AuthenticationInterceptor implements HandlerInterceptor {
	/**
	 * 在Controller方法执行前调用
	 *
	 * @param request  HTTP请求对象
	 * @param response HTTP响应对象
	 * @param handler  将要执行的处理程序（Controller方法）
	 * @return true表示继续执行，false表示中断请求
	 * @throws Exception 可能抛出的异常
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 1. 从请求头获取JWT令牌
		// 约定令牌放在"access-token"请求头中
		String token = request.getHeader("access-token");

		// 2. 验证令牌
		Claims claims = JwtUtil.parseToken(token);

		//解析令牌中的，用户id和用户名
		Long userId = claims.get("userId", Long.class);
		String username = claims.get("username", String.class);

		//添加到线程变量中
		LoginUserHolder.setLoginUser(new LoginUser(userId, username));

		// 3. 验证通过，继续执行后续拦截器和Controller方法
		return true;
	}

	//在Controller方法执行后调用
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		LoginUserHolder.clear();//清理线程变量
	}
}
