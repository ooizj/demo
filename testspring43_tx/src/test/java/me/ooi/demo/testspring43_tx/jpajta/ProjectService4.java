package me.ooi.demo.testspring43_tx.jpajta;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import me.ooi.demo.testhibernate420.po.Project;
import me.ooi.demo.testspring43_tx.jpa.ProjectService;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class ProjectService4 {
	
	@Autowired
	@Qualifier("entityManagerFactory")
	private EntityManagerFactory emf ; 
	
	@Autowired
	@Qualifier("entityManagerFactory3")
	private EntityManagerFactory emf3 ; 
	
	@Transactional
	public int saveProject(String name){
		System.out.println("1 "+TransactionSynchronizationManager.getCurrentTransactionName());
		EntityManager em = emf.createEntityManager() ; 
		Project u = new Project() ; 
		u.setName(name);
		em.persist(u);
		return 1 ; 
	}
	
	@Transactional
	public int saveProject3(String name){
		System.out.println("3 "+TransactionSynchronizationManager.getCurrentTransactionName());
		EntityManager em3 = emf3.createEntityManager() ; 
//		em3.joinTransaction();
		Project u3 = new Project() ; 
		u3.setName(name);
		em3.persist(u3);
		
		System.out.println(3/0);
		
		return 1 ; 
	}
	
	@Transactional
	public int saveProject4(String name){
		System.out.println("4 "+TransactionSynchronizationManager.getCurrentTransactionName());
		ProjectService4 p4 = (ProjectService4)AopContext.currentProxy() ;
		p4.saveProject(name);
		p4.saveProject3(name);
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
			ProjectService4 ps = (ProjectService4) AopContext.currentProxy() ;
			ps.markedAsRollbackErrorTest2("小明");
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
