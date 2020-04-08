package me.ooi.demo.testspring43_tx.hibernate42;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestSpringHibernate42TX {
	
	
	private ClassPathXmlApplicationContext ctx ; 
	private UserService2 userService ; 
	
	@Before
	public void before(){
		ctx = new ClassPathXmlApplicationContext("test-spring-hibernate42-tx.xml") ; 
		userService = ctx.getBean(UserService2.class) ; 
		userService.deleteAllUser();
	}
	
	@After
	public void after(){
		userService.findAllUser();
		ctx.close();
	}
	
	@Test
	public void t1(){
		System.out.println(userService.getClass()); 
	}
	
	@Test
	public void t2(){
		userService.saveUser("xm");
	}
	
	@Test
	public void t3(){
		userService.saveUser2("xm");
	}
	
}