package me.ooi.demo.testhibernate420;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.junit.Test;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestExportDDL {
	
	@Test
	public void exportDDL() {
		Configuration cfg = new Configuration();
		cfg.configure("hibernate.cfg.xml");
		
		// 打印建表语句
		System.out.println("================开始打印建表语句================");
		String[] scripts = cfg.generateSchemaCreationScript(Dialect.getDialect(cfg.getProperties()));
		if (scripts != null) {
			for (String script : scripts) {
				System.out.println(script+";");
			}
		}

		// 打印删除表语句
		System.out.println("================开始打印删除表语句================");
		scripts = cfg.generateDropSchemaScript(Dialect.getDialect(cfg.getProperties()));
		if (scripts != null) {
			for (String script : scripts) {
				System.out.println(script+";");
			}
		}
		
		// 打印更新表语句
		//...
	}

}
