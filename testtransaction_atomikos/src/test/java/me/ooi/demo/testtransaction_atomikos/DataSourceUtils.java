package me.ooi.demo.testtransaction_atomikos;

import java.util.Properties;

import com.atomikos.jdbc.AbstractDataSourceBean;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class DataSourceUtils {
	
	private static AbstractDataSourceBean setDataSourceProperties(AbstractDataSourceBean ds){
		ds.setPoolSize(5);
		ds.setMinPoolSize(3);
		ds.setMaxPoolSize(20);
		ds.setMaxIdleTime(300);
		ds.setMaxLifetime(300);
		return ds ; 
	}
	
	public static AbstractDataSourceBean createTestDataource1(){
		AtomikosDataSourceBean dsb = new AtomikosDataSourceBean() ; 
		setDataSourceProperties(dsb) ; 
		dsb.setUniqueResourceName("ds1");
		dsb.setXaDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		Properties xaProperties = new Properties() ; 
		xaProperties.setProperty("url", "jdbc:mysql://127.0.0.1:3306/mytest?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false") ; 
		xaProperties.setProperty("user", "root") ; 
		xaProperties.setProperty("password", "root") ; 
		dsb.setXaProperties(xaProperties);
		return dsb ; 
	}
	
	public static AbstractDataSourceBean createTestDataource2(){
		AtomikosNonXADataSourceBean dsb = new AtomikosNonXADataSourceBean() ; 
		setDataSourceProperties(dsb) ; 
		dsb.setUniqueResourceName("ds2");
		dsb.setDriverClassName("com.mysql.jdbc.Driver");
		dsb.setUrl("jdbc:mysql://127.0.0.1:3306/mytest2?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false");
		dsb.setUser("root");
		dsb.setPassword("root");
		return dsb ; 
	}
	
}
