package me.ooi.demo.testspring43_tx.jpajta;

import java.sql.Connection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.ooi.demo.testhibernate420.po.Project;
import me.ooi.demo.testspring43_tx.QueryUtils;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class ProjectService2 {
	
	@Autowired
	@Qualifier("entityManagerFactory")
	private EntityManagerFactory emf ; 
	
	@Autowired
	@Qualifier("ds2")
	private DataSource ds2 ; 
	
	@Autowired
	@Qualifier("entityManagerFactory3")
	private EntityManagerFactory emf3 ; 
	
	@Transactional
	public int deleteAllProject() {
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf) ; 
		em.createQuery("delete from Project").executeUpdate() ; 
		
		QueryUtils.deleteAllUser2(DataSourceUtils.getConnection(ds2));
		
		EntityManager em3 = EntityManagerFactoryUtils.getTransactionalEntityManager(emf3) ; 
		em3.createQuery("delete from Project").executeUpdate() ; 
		
		return 1 ; 
	}
	
	@SuppressWarnings("rawtypes")
	@Transactional
	public int findAllProject() {
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf) ; 
		List list = em.createQuery("select u from Project u").getResultList() ; 
		System.out.println(list);
		
		Connection con = DataSourceUtils.getConnection(ds2) ;
		QueryUtils.findAllUser2(con);
		
		EntityManager em3 = EntityManagerFactoryUtils.getTransactionalEntityManager(emf3) ; 
		List list3 = em3.createQuery("select u from Project u").getResultList() ; 
		System.out.println(list3);
		
		return 1 ; 
	}
	
	@Transactional
	public int saveProject(String name){
		
		System.out.println("saveProject:"+Thread.currentThread().getId());
		
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf) ; 
		Project u = new Project() ; 
		u.setName(name);
		em.persist(u);
		
		Connection con = DataSourceUtils.getConnection(ds2) ;
		System.out.println(con); //same in current thread
		QueryUtils.insertUser2(con, name);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {}
		
		System.out.println("saveProject:"+Thread.currentThread().getId()+":ok");
		return 1 ; 
	}
	
	@Transactional
	public int saveProject2(String name){
		
		System.out.println("saveProject2:"+Thread.currentThread().getId());
		
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf) ; 
		Project u = new Project() ; 
		u.setName(name);
		em.persist(u);
		
		Connection con = DataSourceUtils.getConnection(ds2) ;
		System.out.println(con); //same in current thread
		QueryUtils.insertUser2(con, name);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {}
		
		System.out.println("saveProject2:"+Thread.currentThread().getId()+":ok");
		return 1 ; 
	}
	
	@Transactional
	public int save60Project(String name){
		for (int i = 0; i < 60; i++) {
			saveProject(name);
		}
		
		try {
			Thread.sleep(1000*1);
		} catch (InterruptedException e) {}
		return 1 ; 
	}
	
	@Transactional
	public int testRollBack(String name){
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf) ; 
		Project u = new Project() ; 
		u.setName(name);
		em.persist(u);
		
		Connection con = DataSourceUtils.getConnection(ds2) ;
		QueryUtils.insertUser2(con, name);
		
		System.out.println(3/0);
		return 1 ; 
	}
	
	@Transactional
	public int testRollBack2(String name){
		Connection con = DataSourceUtils.getConnection(ds2) ;
		QueryUtils.insertUser2(con, name);
		
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf) ; 
		Project u = new Project() ; 
		u.setName(name);
		em.persist(u);
		
		System.out.println(3/0);
		return 1 ; 
	}
	
}
