package me.ooi.demo.testspring43_tx.jpajta;

import java.util.concurrent.atomic.AtomicInteger;

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
	private ProjectService3 projectService3 ; 
	
	@Before
	public void before(){
		ctx = new ClassPathXmlApplicationContext("test-spring-jpa-jta-tx.xml") ; 
		projectService = ctx.getBean(ProjectService2.class) ; 
		projectService3 = ctx.getBean(ProjectService3.class) ; 
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
	
	@Test
	public void t4(){
		projectService.save60Project("xm");
	}
	
	@Test
	public void testRollBack(){
		projectService.testRollBack("xm");
	}
	
	@Test
	public void testRollBack2(){
		projectService.testRollBack2("xm");
	}
	
	@Test
	public void testConnectionPool() throws InterruptedException{
		AtomicInteger counter = new AtomicInteger(0) ; 
		String name = "hh" ; 
		for (int i = 0; i < 6; i++) {
			counter.incrementAndGet() ; 
			new Thread(()->{
				projectService.save60Project(name) ; 
				counter.decrementAndGet() ; 
			}).start();
		}
		
		//hold on
		while( counter.get() != 0 ){
			Thread.sleep(10);
		}
	}
	
	@Test
	public void t5(){
		projectService3.saveProject4("sss");
	}
	
}