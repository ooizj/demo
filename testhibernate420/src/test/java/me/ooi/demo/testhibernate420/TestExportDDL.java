package me.ooi.demo.testhibernate420;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.junit.Test;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestExportDDL {
	
	@Test
	public void exportJBPM630DDL() {
		Configuration cfg = new Configuration();
		cfg.configure();

		// 打印建表语句
		String[] scripts = cfg.generateSchemaCreationScript(Dialect.getDialect(cfg.getProperties()));
		if (scripts != null) {
			for (String script : scripts) {
				System.out.println(script);
			}
		}

		// 打印删除表的语句
		scripts = cfg.generateDropSchemaScript(Dialect.getDialect(cfg.getProperties()));
		if (scripts != null) {
			for (String script : scripts) {
				System.out.println(script);
			}
		}
	}

}
