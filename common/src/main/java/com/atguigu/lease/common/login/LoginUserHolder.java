package com.atguigu.lease.common.login;

/**
 * 登录用户信息持有器
 * 使用ThreadLocal实现线程级别的用户信息存储
 * 这是典型的线程上下文模式实现，用于在单个请求处理流程中传递用户信息
 */
public class LoginUserHolder {
	// ThreadLocal存储当前线程的LoginUser对象
	public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

	/**
	 * 设置当前线程的登录用户
	 *
	 * @param loginUser 登录用户信息
	 */
	public static void setLoginUser(LoginUser loginUser) {
		threadLocal.set(loginUser);
	}

	/**
	 * 获取当前线程的登录用户
	 * @return 登录用户信息，如果未设置则返回null
	 */
	public static LoginUser getLoginUser() {
		return threadLocal.get();
	}
	/**
	 * 清理当前线程的登录用户信息
	 * 防止内存泄漏和线程复用导致的用户信息混乱
	 */
	public static void clear() {
		threadLocal.remove();
	}
}