package me.ooi.demo.testspring43.aop;

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
@ContextConfiguration("/test-spring-aop.xml")
public class TestSpringAop {
	
	@Autowired
	private UserService userService ; 
	
	@Test
	public void t1(){
		System.out.println(userService.getClass()); //class me.ooi.demo.testspring43.aop.UserService$$EnhancerBySpringCGLIB$$c5f19e5e
	}
	
	@Test
	public void t2(){
		userService.saveUser("xm");
	}
	
	@Test
	public void t3(){
		userService.updateUser("xm");
	}
	
}