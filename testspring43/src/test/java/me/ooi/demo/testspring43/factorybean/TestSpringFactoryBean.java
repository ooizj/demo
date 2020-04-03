package me.ooi.demo.testspring43.factorybean;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import me.ooi.demo.testspring43.User;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-factorybean.xml")
public class TestSpringFactoryBean {
	
	@Autowired
	private User u1 ; 

	@Autowired
	private User u2 ; 
	
	@Test
	public void t1(){
		System.out.println(u1); 
		System.out.println(u2); 
	}
	
}
