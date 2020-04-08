package me.ooi.demo.testspring43_tx.hibernate42;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.ooi.demo.testhibernate420.po.User;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class UserService2 {
	
	@Autowired
	private SessionFactory sessionFactory ; 
	
	@Transactional
	public int deleteAllUser() {
		Session session = sessionFactory.getCurrentSession() ;
		session.createQuery("delete from User").executeUpdate() ; 
//		session.createQuery("delete from Project").executeUpdate() ; 
		return 1 ; 
	}
	
	@SuppressWarnings("rawtypes")
	@Transactional
	public int findAllUser() {
		Session session = sessionFactory.getCurrentSession() ;
		List list = session.createCriteria(User.class).list() ; 
		System.out.println(list);
		return 1 ; 
	}
	
	@Transactional
	public int saveUser(String name){
		Session session = sessionFactory.getCurrentSession() ;
		User u = new User() ; 
		u.setName(name);
		session.save(u) ; 
		return 1 ; 
	}
	
	@Transactional
	public int saveUser2(String name){
		Session session = sessionFactory.getCurrentSession() ;
		User u = new User() ; 
		u.setName(name);
		session.save(u) ; 
		System.out.println(3/0);
		return 1 ; 
	}
	
}
