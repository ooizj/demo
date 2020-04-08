package me.ooi.demo.testspring43_tx.jpajta;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestSpringJpaJtaTX {
	
	private ClassPathXmlApplicationContext ctx ; 
	private ProjectService2 projectService ; 
	
	@Before
	public void before(){
		ctx = new ClassPathXmlApplicationContext("test-spring-jpa-jta-tx.xml") ; 
		projectService = ctx.getBean(ProjectService2.class) ; 
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
		projectService.testRollBack("xm");
	}
	
	@Test
	public void t4(){
		projectService.testRollBack2("xm");
	}
	
	@Test
	public void t5(){
		projectService.testConnectionPool("xm");
	}
	
	@Test
	public int testConnectionPool(String name){
		for (int i = 0; i < 6; i++) {
			projectService.saveProject(name) ; 
			
			try {
				Thread.sleep(1*1);
			} catch (InterruptedException e) {}
		}
		return 1 ; 
	}
	
}