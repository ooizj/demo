package me.ooi.demo.testtransaction_hibernate420.jdbc;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.transaction.spi.TransactionFactory;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.ooi.demo.testhibernate420.po.User;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestJdbcTransaction {
	
	private SessionFactory sessionFactory ;
	
	@Before
	public void init(){
		Configuration cfg = new Configuration() ; 
    	cfg.configure("hibernate-jdbc.cfg.xml") ; 
    	ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry() ; 
    	
    	Object obj = sr.getService(TransactionFactory.class) ; 
    	System.out.println(obj);
    	
    	sessionFactory = cfg.buildSessionFactory(sr) ; 
	}
	
	@After
	public void destroy(){
		sessionFactory.close();
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void t1(){
		Session sess = sessionFactory.openSession() ; 
		System.out.println(sess);
		
		Transaction tx = null ;
		
		try {
			tx = sess.beginTransaction() ; 
			
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
			
//			int a = 3/0;
			
			tx.commit();
		} catch (Exception e) {
			if( tx != null ){
				tx.rollback();
			}
			e.printStackTrace();
		} finally{
			sess.close() ; 
		}
	}
	
}
