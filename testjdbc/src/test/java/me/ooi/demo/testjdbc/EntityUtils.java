package me.ooi.demo.testjdbc;

import com.google.common.base.CaseFormat;

/**
 * @author jun.zhao
 */
public class EntityUtils {
	
	/**
	 * javabean字段名转换为数据库字段名，采用驼峰转下划线的方式（如：“userId”会转为“user_id”）
	 * @param beanField
	 * @return
	 */
	public static String beanFieldToDbField(String beanField) {
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, beanField);
	}
	
	/**
	 * javabean字段名转换为数据库字段名，采用驼峰转下划线的方式（如：“userId”会转为“user_id”）
	 * @param beanField
	 * @return
	 */
	public static String dbFieldToBeanField(String beanField) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, beanField);
	}

}
