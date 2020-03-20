package me.ooi.demo.testhibernate420;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import me.ooi.demo.testhibernate420.po.User;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestHibernateSessionCache {

	@SuppressWarnings("rawtypes")
	@Test
	public void t1(){
		Session sess = HibernateUtils.getSessionFactory().openSession() ; 
		sess.beginTransaction() ; 
		
		Query query = sess.createQuery("select u from User u ") ; 
		List list = query.list() ; 
		System.out.println(list);
		
		int id = ((User)list.get(0)).getId() ; 
		
		User u = (User) sess.get(User.class, id) ; //没有查询数据库
		System.out.println(u);
		
		User u2 = (User) sess.get(User.class, id) ;//没有查询数据库
		System.out.println(u2);
		
		sess.getTransaction().commit();
		sess.close() ; 
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void t2(){
		Session sess = HibernateUtils.getSessionFactory().openSession() ; 
		sess.beginTransaction() ; 
		
		Query query = sess.createQuery("select u from User u ") ; 
		List list = query.list() ; 
		System.out.println(list);
		
		int id = ((User)list.get(0)).getId() ; 
		
		User u = (User) sess.get(User.class, id) ; //没有查询数据库
		System.out.println(u);
		
		query = sess.createQuery("select u from User u where u.id = ?") ; //有查询数据库
		query.setParameter(0, id) ; 
		list = query.list() ; 
		System.out.println(list);
		
		u = (User) sess.createCriteria(User.class, "u")
				.add(Restrictions.eq("u.id", id))
				.uniqueResult() ; //有查询数据库
		System.out.println(u);
		
		sess.getTransaction().commit();
		sess.close() ; 
	}
	
}
