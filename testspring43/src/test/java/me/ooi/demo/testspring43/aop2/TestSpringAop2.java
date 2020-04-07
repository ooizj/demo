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
	private UserService2 userService ; 
	
	@Test
	public void t1(){
		System.out.println(userService.getClass()); //class me.ooi.demo.testspring43.aop2.UserService2$$EnhancerBySpringCGLIB$$20d0b04e
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