package me.ooi.demo.testspring43.aop2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-aop2.xml")
public class TestSpringAop2 {
	
	@Autowired
	private UserService2 userService2 ; 
	
	@Test
	public void t1(){
		System.out.println(userService2.getClass()); 
	}
	
}