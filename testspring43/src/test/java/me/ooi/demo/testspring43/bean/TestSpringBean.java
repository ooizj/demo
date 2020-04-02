package me.ooi.demo.testspring43.bean;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import me.ooi.demo.testspring43.World;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-bean.xml")
public class TestSpringBean {
	
	@Autowired
	private World w ; 
	
	@Autowired
	private World w2 ; 

	@Test
	public void t1(){
		System.out.println(w); //World [users=[
			//me.ooi.demo.testspring43.User@6295d394, 
			//me.ooi.demo.testspring43.User@475e586c, 
			//me.ooi.demo.testspring43.User@657c8ad9, 
			//me.ooi.demo.testspring43.User@657c8ad9]]

		System.out.println(w==w2); //true
	}
	
}
