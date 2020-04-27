package me.ooi.demo.testspring43_tx.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestSpringJdbcTX {
	
	private ClassPathXmlApplicationContext ctx ; 
	private UserService userService ; 
	
	@Before
	public void before(){
		ctx = new ClassPathXmlApplicationContext("test-spring-jdbc-tx.xml") ; 
		userService = ctx.getBean(UserService.class) ; 
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
	
	@Test
	public void testRegisterSynchronization(){
		userService.testRegisterSynchronization("ff") ; 
		System.out.println("hello");
	}
	
}