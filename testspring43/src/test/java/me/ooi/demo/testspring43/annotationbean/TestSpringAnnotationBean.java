package me.ooi.demo.testspring43.annotationbean;

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
@ContextConfiguration("/test-spring-annotationbean.xml")
public class TestSpringAnnotationBean {
	
	@Autowired
	private World w ; 
	
	@Autowired
	private World w2 ; 

	@Test
	public void t1(){
		System.out.println(w); //World [users=[
			//me.ooi.demo.testspring43.User@79e2c065, 
			//me.ooi.demo.testspring43.User@3a93b025, 
			//me.ooi.demo.testspring43.User@35cabb2a, 
			//me.ooi.demo.testspring43.User@35cabb2a]]

		System.out.println(w==w2); //true
	}
	
}
