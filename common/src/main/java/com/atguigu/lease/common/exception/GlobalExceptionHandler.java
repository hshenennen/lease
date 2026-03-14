package com.atguigu.lease.common.exception;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.common.result.ResultCodeEnum;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
//全局异常处理
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public Result error(Exception e) {
		e.printStackTrace();
		return Result.fail();
	}


	//删除子集错误处理
	@ExceptionHandler(LeaseException.class)
	@ResponseBody
	public Result error(LeaseException e) {
		e.printStackTrace();
		return Result.fail(e.getMessage(), e.getCode());
	}
}
