package com.atguigu.lease.common.utils;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
	// 60 * 60 * 1000 = 3600000毫秒 = 1小时
	private static Long tokenExpiration = 60 * 60 * 1000L;//token的过期时间
	private static SecretKey tokenSignKey = Keys.hmacShaKeyFor("x6WpAepeA6j4uaRa5sGnJ2RcYZeS3VhV".getBytes());//token的密码

	/**
	 * 创建JWT令牌
	 *
	 * @param userId   用户ID - 存储在JWT的payload中
	 * @param username 用户名 - 存储在JWT的payload中
	 * @return 返回生成的JWT字符串
	 */
	public static String createToken(Long userId, String username) {
		String token = Jwts.builder()
				.setSubject("USER_INFO")// // 设置主题，这里固定为"USER_INFO"
				.setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))//设置token的过期时间
				.claim("userId", userId)// 添加自定义声明(claims)：存储用户信息
				.claim("username", username)// claim()方法可以添加任意自定义数据
				.signWith(tokenSignKey) // 使用密钥进行签名（确保令牌不被篡改）
				.compact();// 生成最终的JWT字符串

		return token;
	}

	/**
	 * 解析JWT令牌的方法
	 * 该方法负责验证JWT的有效性并返回令牌中的声明(Claims)
	 *
	 * @param token JWT令牌字符串
	 * @return JWT中的声明信息(Claims)
	 * @throws LeaseException 当令牌无效或过期时抛出自定义异常
	 */
	public static Claims parseToken(String token) {
		// 空值检查 - 如果令牌为空，直接抛出异常
		if (token == null) {
			// 抛出需要登录的自定义异常，提示用户重新登录
			throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
		}
		try {
			// 构建JWT解析器
			// JWT解析器使用相同的密钥进行验证，确保令牌未被篡改
			JwtParser jwtParser = Jwts.parserBuilder()
					.setSigningKey(tokenSignKey)//// 设置签名密钥（必须与生成时相同）
					.build();

			//解析令牌并返回声明
			// parseClaimsJws()会验证签名并解析JWT
			// getBody()获取JWT的payload部分（即声明信息）-- userId和username
			return jwtParser.parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {//处理令牌过期异常
			throw new LeaseException(ResultCodeEnum.TOKEN_EXPIRED);
		} catch (JwtException e) {
			// 处理其他JWT异常，
			throw new LeaseException(ResultCodeEnum.TOKEN_INVALID);
		}
	}

	public static void main(String[] args) {
		System.out.println(createToken(1L, "13888888888"));
	}
}
