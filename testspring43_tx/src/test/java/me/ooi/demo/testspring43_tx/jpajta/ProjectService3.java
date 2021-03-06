package me.ooi.demo.testspring43_tx.jpajta;

import java.sql.Connection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import me.ooi.demo.testhibernate420.po.Project;
import me.ooi.demo.testspring43_tx.QueryUtils;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class ProjectService3 {
	
	@Autowired
	@Qualifier("entityManagerFactory")
	private EntityManagerFactory emf ; 
	
	@Autowired
	@Qualifier("ds2")
	private DataSource ds2 ; 
	
	@Autowired
	@Qualifier("entityManagerFactory3")
	private EntityManagerFactory emf3 ; 
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public int saveProject(String name){
		System.out.println("1 "+TransactionSynchronizationManager.getCurrentTransactionName());
		EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf) ; 
		Project u = new Project() ; 
		u.setName(name);
		em.persist(u);
		return 1 ; 
	}
	
	@Transactional
	public int saveProject2(String name){
		System.out.println("2 "+TransactionSynchronizationManager.getCurrentTransactionName());
		Connection con = DataSourceUtils.getConnection(ds2) ;
		System.out.println(con); //same in current thread
		QueryUtils.insertUser2(con, name);
		return 1 ; 
	}
	
	@Transactional
	public int saveProject3(String name){
		System.out.println("3 "+TransactionSynchronizationManager.getCurrentTransactionName());
		EntityManager em3 = EntityManagerFactoryUtils.getTransactionalEntityManager(emf3) ; 
		Project u3 = new Project() ; 
		u3.setName(name);
		em3.persist(u3);
		
		System.out.println(3/0);
		
		return 1 ; 
	}
	
	@Transactional
	public int saveProject4(String name){
		System.out.println("4 "+TransactionSynchronizationManager.getCurrentTransactionName());
		ProjectService3 p3 = (ProjectService3)AopContext.currentProxy() ;
		p3.saveProject(name);
		p3.saveProject2(name);
		p3.saveProject3(name);
		return 1 ; 
	}
	
}
