package me.ooi.demo.testnarayana510;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.XAConnection;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arjuna.ats.internal.jdbc.drivers.PropertyFileDynamicClass;
import com.arjuna.ats.jdbc.TransactionalDriver;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestNarayana {
	
	private MysqlXADataSource dsXA;
	private MysqlXADataSource dsXA2;
	
	@BeforeEach
	private void init() {
		dsXA = new MysqlXADataSource();
		dsXA.setUrl("jdbc:mysql://localhost:3306/mytest");
		dsXA.setUser("root");
		dsXA.setPassword("root");
		
		dsXA2 = new MysqlXADataSource();
		dsXA2.setUrl("jdbc:mysql://localhost:3306/mytest2");
		dsXA2.setUser("root");
		dsXA2.setPassword("root");
	}
	
	@AfterEach
	private void destroy() {
	}

	@Test
	public void t1()
			throws NotSupportedException, SystemException, SQLException, IllegalStateException, RollbackException {
		
		// here we get instance of Narayana transaction manager
		TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		// and beginning the global transaction for XAResources could be enlisted into
		tm.begin();

		// from XADataSource getting XAConnection and then the XAResource
		XAConnection xaConn = dsXA.getXAConnection();
		// transaction manager to be provided with the XAResource
		tm.getTransaction().enlistResource(xaConn.getXAResource());
		
		XAConnection xaConn2 = dsXA2.getXAConnection();
		tm.getTransaction().enlistResource(xaConn2.getXAResource());

		// statement executed and transaction is committed or rolled-back depending of
		// the result
		try {

			QueryUtils.insertUser2(xaConn.getConnection(), "xiaoming");
			QueryUtils.insertUser2(xaConn2.getConnection(), "xiaoming");
			QueryUtils.insertUser2(xaConn.getConnection(), "xiaohong");
			QueryUtils.insertUser2(xaConn2.getConnection(), "xiaohong");
			
//			int a = 3/0;

			System.out.println("commit");
			tm.commit();
		} catch (Exception e) {
			System.out.println("rollback");
			tm.rollback();
		} finally {
			xaConn.close(); // omitting try-catch block
			xaConn2.close();
		}
	}
	
	//Managing the transaction enlistment with use of the JDBC transactional driver
	@Test
	public void testNarayanaJDBC()
			throws NotSupportedException, SystemException, SQLException, IllegalStateException, RollbackException {
		
		// the datasource is put as property with the special name
		Properties connProperties = new Properties();
		connProperties.put(TransactionalDriver.XADataSource, dsXA);
		// getting connection when the 'url' is 'jdbc:arjuna' prefix which determines
		// the Naryana drive to be used
		Connection con = DriverManager.getConnection(TransactionalDriver.arjunaDriver, connProperties);
		
		Properties connProperties2 = new Properties();
		connProperties2.put(TransactionalDriver.XADataSource, dsXA2);
		Connection con2 = DriverManager.getConnection(TransactionalDriver.arjunaDriver, connProperties2);
		
		// here we get instance of Narayana transaction manager
		TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		// and beginning the global transaction for XAResources could be enlisted into
		tm.begin();

		// statement executed and transaction is committed or rolled-back depending of
		// the result
		try {

			QueryUtils.insertUser2(con, "xiaoming");
			QueryUtils.insertUser2(con2, "xiaoming");
			QueryUtils.insertUser2(con, "xiaohong");
			QueryUtils.insertUser2(con2, "xiaohong");
			
//			int a = 3/0;

			System.out.println("commit");
			tm.commit();
		} catch (Exception e) {
			System.out.println("rollback");
			tm.rollback();
		} finally {
			con.close(); // omitting try-catch block
			con2.close();
		}
	}
	
	@Test
	public void testNarayanaJDBC2()
			throws NotSupportedException, SystemException, SQLException, IllegalStateException, RollbackException, NamingException {
		
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, TestInitialContextFactory.class.getName());
		
		// binding xa datasource to JNDI name 'ds'
		InitialContext ctx = new InitialContext();
		System.out.println(ctx);
		ctx.bind("ds", dsXA);
		ctx.bind("ds2", dsXA2);
		
		// passing the JNDI name 'ds' as part of the connection url that we demand
		// the first part is narayana transactional driver prefix
		String dsJndi = TransactionalDriver.arjunaDriver + "ds"; 
		
		//确保是否注册com.arjuna.ats.jdbc.TransactionalDriver.TransactionalDriver
		if( !Collections.list(DriverManager.getDrivers()).stream().map(
				d->d.getClass()).collect(Collectors.toList()).contains(TransactionalDriver.class) ) {
			DriverManager.registerDriver(new TransactionalDriver());
		}
		
		Connection con = DriverManager.getConnection(dsJndi, new Properties());
		Connection con2 = DriverManager.getConnection(TransactionalDriver.arjunaDriver + "ds2", new Properties());
		
		// here we get instance of Narayana transaction manager
		TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		// and beginning the global transaction for XAResources could be enlisted into
		tm.begin();

		// statement executed and transaction is committed or rolled-back depending of
		// the result
		try {

			QueryUtils.insertUser2(con, "xiaoming");
			QueryUtils.insertUser2(con2, "xiaoming");
			QueryUtils.insertUser2(con, "xiaohong");
			QueryUtils.insertUser2(con2, "xiaohong");
			
//			int a = 3/0;

			System.out.println("commit");
			tm.commit();
		} catch (Exception e) {
			System.out.println("rollback");
			tm.rollback();
		} finally {
			con.close(); // omitting try-catch block
			con2.close();
		}
	}

	@Test
	public void testNarayanaJDBC2_2()
			throws NotSupportedException, SystemException, SQLException, IllegalStateException, RollbackException, NamingException {
		
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, TestInitialContextFactory.class.getName());
		
		// binding xa datasource to JNDI name 'ds'
		InitialContext ctx = new InitialContext();
		ctx.bind("ds", dsXA);
		ctx.bind("ds2", dsXA2);
		
		// passing the JNDI name 'ds' as part of the connection url that we demand
		// the first part is narayana transactional driver prefix
		TransactionalDriver driver = new TransactionalDriver();
		Connection con = driver.connect(TransactionalDriver.arjunaDriver + "ds", new Properties());
		Connection con2 = driver.connect(TransactionalDriver.arjunaDriver + "ds2", new Properties());
		
		// here we get instance of Narayana transaction manager
		TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		// and beginning the global transaction for XAResources could be enlisted into
		tm.begin();

		// statement executed and transaction is committed or rolled-back depending of
		// the result
		try {

			QueryUtils.insertUser2(con, "xiaoming");
			QueryUtils.insertUser2(con2, "xiaoming");
			QueryUtils.insertUser2(con, "xiaohong");
			QueryUtils.insertUser2(con2, "xiaohong");
			
//			int a = 3/0;

			System.out.println("commit");
			tm.commit();
		} catch (Exception e) {
			System.out.println("rollback");
			tm.rollback();
		} finally {
			con.close(); // omitting try-catch block
			con2.close();
		}
	}
	
	@Test
	public void testNarayanaJDBC2_3()
			throws NotSupportedException, SystemException, SQLException, IllegalStateException, RollbackException, NamingException {
		
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, TestInitialContextFactory.class.getName());
		
		// binding xa datasource to JNDI name 'ds'
		InitialContext ctx = new InitialContext();
		ctx.bind("ds", dsXA);
		ctx.bind("ds2", dsXA2);
		
		// passing the JNDI name 'ds' as part of the connection url that we demand
		// the first part is narayana transactional driver prefix
		TransactionalDriver driver = new TransactionalDriver();
		
		Properties props1 = new Properties();
        props1.put(TransactionalDriver.userName, "root");
        props1.put(TransactionalDriver.password, "root");
        props1.put(TransactionalDriver.poolConnections, "true");
        props1.put(TransactionalDriver.maxConnections, "50"); // JBTM-2976
        
        Properties props2 = props1; //same
		
		Connection con = driver.connect(TransactionalDriver.arjunaDriver + "ds", props1);
		Connection con2 = driver.connect(TransactionalDriver.arjunaDriver + "ds2", props2);
		
		// here we get instance of Narayana transaction manager
		TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		// and beginning the global transaction for XAResources could be enlisted into
		tm.begin();

		// statement executed and transaction is committed or rolled-back depending of
		// the result
		try {

			QueryUtils.insertUser2(con, "xiaoming");
			QueryUtils.insertUser2(con2, "xiaoming");
			QueryUtils.insertUser2(con, "xiaohong");
			QueryUtils.insertUser2(con2, "xiaohong");
			
//			int a = 3/0;

			System.out.println("commit");
			tm.commit();
		} catch (Exception e) {
			System.out.println("rollback");
			tm.rollback();
		} finally {
			con.close(); // omitting try-catch block
			con2.close();
		}
	}
	
	//XADataSource connection data provided in properties file
	@Test
	public void testNarayanaJDBC3() throws SQLException, NotSupportedException, SystemException {
		
		// jdbc url is defined with path to the properties file
		// see the ./ds.properties as 'getConnection' url parameter
		Properties props = new Properties();
		props.put(TransactionalDriver.dynamicClass, PropertyFileDynamicClass.class.getName());
		
		//确保是否注册com.arjuna.ats.jdbc.TransactionalDriver.TransactionalDriver
		if( !Collections.list(DriverManager.getDrivers()).stream().map(
				d->d.getClass()).collect(Collectors.toList()).contains(TransactionalDriver.class) ) {
			DriverManager.registerDriver(new TransactionalDriver());
		}
		
		String path1 = Thread.currentThread().getContextClassLoader().getResource("ds.properties").getFile();
		String path2 = Thread.currentThread().getContextClassLoader().getResource("ds2.properties").getFile();
		
		Connection con = DriverManager.getConnection(TransactionalDriver.arjunaDriver + path1, props);
		Connection con2 = DriverManager.getConnection(TransactionalDriver.arjunaDriver + path2, props);
		
		// here we get instance of Narayana transaction manager
		TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		// and beginning the global transaction for XAResources could be enlisted into
		tm.begin();

		// statement executed and transaction is committed or rolled-back depending of
		// the result
		try {

			QueryUtils.insertUser2(con, "xiaoming");
			QueryUtils.insertUser2(con2, "xiaoming");
			QueryUtils.insertUser2(con, "xiaohong");
			QueryUtils.insertUser2(con2, "xiaohong");
			
//			int a = 3/0;

			System.out.println("commit");
			tm.commit();
		} catch (Exception e) {
			System.out.println("rollback");
			tm.rollback();
		} finally {
			con.close(); // omitting try-catch block
			con2.close();
		}
	}

}
