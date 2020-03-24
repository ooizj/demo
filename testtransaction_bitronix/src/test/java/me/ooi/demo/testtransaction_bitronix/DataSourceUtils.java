package me.ooi.demo.testtransaction_bitronix;

import java.util.Properties;

import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class DataSourceUtils {
	
	private static PoolingDataSource setDataSourceProperties(PoolingDataSource ds){
		ds.setAcquireIncrement(1);
		ds.setAcquisitionInterval(1);
		ds.setAcquisitionTimeout(30);
		ds.setAllowLocalTransactions(true);
		ds.setAutomaticEnlistingEnabled(true);
		ds.setEnableJdbc4ConnectionTest(true);
		ds.setIgnoreRecoveryFailures(false);
		ds.setIsolationLevel("READ_COMMITTED");
		ds.setMaxIdleTime(60);
		ds.setMaxLifeTime(300);
		ds.setMaxPoolSize(20);
		ds.setMinPoolSize(10);
		ds.setPreparedStatementCacheSize(0);
		ds.setShareTransactionConnections(false);
		ds.setUseTmJoin(true);
		return ds ; 
	}
	
	public static PoolingDataSource createTestDataource1() {
		PoolingDataSource ds = new PoolingDataSource() ; 
		Properties p = new Properties() ; 
		p.setProperty("url", "jdbc:mysql://127.0.0.1:3306/mytest?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false") ; 
		p.setProperty("user", "root") ; 
		p.setProperty("password", "root") ; 
		ds.setDriverProperties(p);
		ds.setClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		setDataSourceProperties(ds) ; 
		ds.setUniqueName("jdbc/btm-ds1");
		return ds ; 
	}
	
	public static PoolingDataSource createTestDataource2() {
		PoolingDataSource ds = new PoolingDataSource() ; 
		Properties p = new Properties() ; 
		p.setProperty("url", "jdbc:mysql://127.0.0.1:3306/mytest2?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false") ; 
		p.setProperty("user", "root") ; 
		p.setProperty("password", "root") ; 
		ds.setDriverProperties(p);
		ds.setClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		setDataSourceProperties(ds) ; 
		ds.setUniqueName("jdbc/btm-ds2");
		return ds ; 
	}
	
}
