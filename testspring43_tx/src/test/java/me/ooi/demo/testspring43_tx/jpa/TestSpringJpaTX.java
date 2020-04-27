package me.ooi.demo.testspring43_tx.jpa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestSpringJpaTX {
	
	private ClassPathXmlApplicationContext ctx ; 
	private ProjectService projectService ; 
	
	@Before
	public void before(){
		ctx = new ClassPathXmlApplicationContext("test-spring-jpa-tx.xml") ; 
		projectService = ctx.getBean(ProjectService.class) ; 
		projectService.deleteAllProject();
	}
	
	@After
	public void after(){
		projectService.findAllProject();
		ctx.close();
	}
	
	@Test
	public void t1(){
		System.out.println(projectService.getClass()); 
	}
	
	@Test
	public void t2(){
		projectService.saveProject("xm");
	}
	
	@Test
	public void t3(){
		projectService.saveProject2("xm");
	}
	
	//test for org.springframework.transaction.TransactionSystemException: Could not commit JPA transaction; nested exception is javax.persistence.RollbackException: Transaction marked as rollbackOnly
	@Test
	public void markedAsRollbackErrorTest(){
		projectService.markedAsRollbackErrorTest();
	}
	
}