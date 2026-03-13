package com.atguigu.lease.common.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
//当写入数据时，Mybatis-Plus会自动将实体对象的create_time字段填充为当前时间，
// 当更新数据时，则会自动将实体对象的update_time字段填充为当前时间。
public class MybatisMetaObjectHandler implements MetaObjectHandler {
	@Override
	public void insertFill(MetaObject metaObject) {
		this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
	}
}
