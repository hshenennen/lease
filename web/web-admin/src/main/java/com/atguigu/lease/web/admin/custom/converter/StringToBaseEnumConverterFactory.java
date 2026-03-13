package com.atguigu.lease.web.admin.custom.converter;

import com.atguigu.lease.model.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

@Component
//枚举转换
public class StringToBaseEnumConverterFactory implements ConverterFactory<String, BaseEnum> {
	@Override
	public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
		return new Converter<String, T>() {
			@Override
			public T convert(String source) {

				for (T enumConstant : targetType.getEnumConstants()) {//targetType.getEnumConstants()反射，获取枚举中所有的类型
					if (enumConstant.getCode().equals(Integer.valueOf(source))) {//Integer.valueOf转换Integer类型
						return enumConstant;
					}
				}
				throw new IllegalArgumentException("非法的枚举值:" + source);
			}
		};
	}
}
