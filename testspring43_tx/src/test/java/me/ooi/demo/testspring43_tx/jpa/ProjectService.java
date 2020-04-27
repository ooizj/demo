package me.ooi.demo.testspring43_tx.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import me.ooi.demo.testhibernate420.po.Project;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class ProjectService {
	
	@Autowired
	private EntityManagerFactory emf ; 
	
	@Transactional
	public int deleteAllProject() {
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf) ; 
		em.createQuery("delete from Project").executeUpdate() ; 
		return 1 ; 
	}
	
	@SuppressWarnings("rawtypes")
	@Transactional
	public int findAllProject() {
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf) ; 
		List list = em.createQuery("select u from Project u").getResultList() ; 
		System.out.println(list);
		return 1 ; 
	}
	
	@Transactional
	public int saveProject(String name){
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf) ; 
		Project u = new Project() ; 
		u.setName(name);
		em.persist(u);
		return 1 ; 
	}
	
	@Transactional
	public int saveProject2(String name){
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf) ; 
		Project u = new Project() ; 
		u.setName(name);
		em.persist(u);
		
		System.out.println(3/0);
		return 1 ; 
	}
	
	
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true
//		, propagation=Propagation.NEVER
	)
	public int markedAsRollbackErrorTest() {
		
		System.out.println("事务名称："+TransactionSynchronizationManager.getCurrentTransactionName());
		
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf) ; 
		List list = em.createQuery("select u from Project u").getResultList() ; 
		System.out.println(list);
		
		try {
			ProjectService ps = (ProjectService) AopContext.currentProxy() ;
			ps.markedAsRollbackErrorTest2("xiaoming");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return 1 ; 
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public int markedAsRollbackErrorTest2(String name){
		
		System.out.println("事务名称："+TransactionSynchronizationManager.getCurrentTransactionName());
		
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf) ; 
		Project u = new Project() ; 
		u.setName(name);
		em.persist(u);
		
		throw new RuntimeException("test error");
	}
	
}
