package me.ooi.demo.testtransaction_hibernate420.bitronix;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.transaction.spi.TransactionFactory;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.jta.platform.spi.JtaPlatform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import me.ooi.demo.testhibernate420.po.User;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestBitronixTransactionSetDataSourceByXML {
	
	private SessionFactory sessionFactory ;
	private PoolingDataSource ds1 ; 
	private PoolingDataSource ds2 ; 
	
	@Before
	public void init(){
		ds1 = DataSourceUtils.createTestDataource1() ; 
		ds1.init();
		
		ds2 = DataSourceUtils.createTestDataource2() ; 
		ds2.init();
		
		//init hibernate
		Configuration cfg = new Configuration() ; 
    	cfg.configure("hibernate-bitronix.cfg.xml") ; 
    	ServiceRegistry sr = new ServiceRegistryBuilder()
    			.applySettings(cfg.getProperties())
    			.buildServiceRegistry() ; 
    	
    	Object obj = sr.getService(TransactionFactory.class) ; 
    	System.out.println(obj);
    	
    	obj = sr.getService(JtaPlatform.class) ; 
    	System.out.println(obj);
    	
    	sessionFactory = cfg.buildSessionFactory(sr) ; 
	}
	
	@After
	public void destroy(){
		sessionFactory.close();
		
		ds1.close();
		ds2.close();
		
		TransactionManagerServices.getTransactionManager().shutdown();
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void t1() throws Exception{
		try {
			Session sess = sessionFactory.openSession() ; 
			System.out.println(sess);
			
			System.out.println(sess.createQuery("select u from User u").list());
			
//			empty user 
			sess.createQuery("delete from User").executeUpdate() ; 
			
			//test save
			User u = new User() ; 
			u.setAge(1);
			u.setName("tom");
			sess.save(u) ; 
			
			//test select
			List userList = sess.createQuery("select u from User u").list() ; 
			System.out.println(userList);
			
			int a = 3/0;
			
			sess.close() ; 
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void t2() throws Exception{
		final BitronixTransactionManager ut = TransactionManagerServices.getTransactionManager();
		
		try {
			ut.begin();
			
			Session sess = sessionFactory.openSession() ; 
			System.out.println(sess);
			
			System.out.println(sess.createQuery("select u from User u").list());
			
//			empty user 
			sess.createQuery("delete from User").executeUpdate() ; 
			
			//test save
			User u = new User() ; 
			u.setAge(1);
			u.setName("tom");
			sess.save(u) ; 
			
			//test select
			List userList = sess.createQuery("select u from User u").list() ; 
			System.out.println(userList);
			
			int a = 3/0;
			
			sess.close() ; 
			
			ut.commit();
		} catch (Exception e) {
			e.printStackTrace();
			ut.rollback();
		} 
	}
	
}
