package me.ooi.demo.testhibernate420;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import me.ooi.demo.testhibernate420.po.Project;
import me.ooi.demo.testhibernate420.po.User;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestHibernate {
	
	@SuppressWarnings("rawtypes")
	@Test
	public void t1(){
		Session sess = HibernateUtils.getSessionFactory().openSession() ; 
		
		Transaction tx = null ;
		
		try {
			tx = sess.beginTransaction() ; 
			
			System.out.println(sess.createQuery("select u from User u").list());
			
			//empty user 
//			sess.createQuery("delete from User").executeUpdate() ; 
			
			//test save
			User u = new User() ; 
			u.setAge(1);
			u.setName("tom");
			sess.save(u) ; 
			
			//test select
			List userList = sess.createQuery("select u from User u").list() ; 
			System.out.println(userList);
			
//			int a = 3/0;
			
//			sess.delete(u);
			
			tx.commit();
		} catch (Exception e) {
			if( tx != null ){
				tx.rollback();
			}
			e.printStackTrace();
		} finally{
			sess.close() ; 
			HibernateUtils.getSessionFactory().close();
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void t2(){
		Session sess = HibernateUtils.getSessionFactory().openSession() ; 
		
		Transaction tx = null ;
		
		try {
			tx = sess.beginTransaction() ; 
			
			System.out.println(sess.createQuery("select p from Project p").list());
			
			//test save
			Project p = new Project() ; 
			p.setName("p1");
			sess.save(p) ; 
			
			//test select
			List list = sess.createQuery("select p from Project p").list() ; 
			System.out.println(list);
			
			tx.commit();
		} catch (Exception e) {
			if( tx != null ){
				tx.rollback();
			}
			e.printStackTrace();
		} finally{
			sess.close() ; 
			HibernateUtils.getSessionFactory().close();
		}
	}

}
