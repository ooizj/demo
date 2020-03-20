package me.ooi.demo.testjpa_hibernate;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.spi.PersistenceProvider;

import org.hibernate.ejb.HibernatePersistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.ooi.demo.testjpa_hibernate.po.User;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestJPA {
	
	private EntityManagerFactory emf ;
	
	@Before
	public void init(){
		PersistenceProvider provider = new HibernatePersistence() ; 
		Map<String, Object> properties = new HashMap<>() ; 
		emf = provider.createEntityManagerFactory("poi", properties) ; 
	}
	
	@After
	public void destroy(){
		if( emf != null ){
			emf.close();
		}
	}
	
	@Test
	public void t1(){
		EntityManager em = emf.createEntityManager() ; 
		EntityTransaction tx = em.getTransaction() ; 
		try {
			
			tx.begin(); //start transaction

			//test save
			User u = new User() ; 
			u.setAge(1);
			u.setName("json");
			em.persist(u) ; 
			
			//test select
			System.out.println(em.createQuery("select u from User u").getResultList());
			System.out.println(em.createQuery("select p from Project p").getResultList());
			
			tx.commit(); //commit transaction
		} catch (Exception e) {
			tx.rollback(); //rollback transaction
			e.printStackTrace();
		}
		
		em.close();
		
	}

}
