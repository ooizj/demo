package me.ooi.demo.testspring43.beanname;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-spring-beanname.xml")
public class TestSpringBeanName {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Test
	public void t1(){
		System.out.println(applicationContext.getBean(InterfaceA.class)); //org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type 'me.ooi.demo.testspring43.beanname.InterfaceA' available: expected single matching bean but found 2: componentA1,componentA2
	}
	
	@Test
	public void t2(){
		System.out.println(applicationContext.getBean(ComponentA1.class)); //org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type 'me.ooi.demo.testspring43.beanname.ComponentA1' available: expected single matching bean but found 2: componentA1,componentA2
	}
	
	@Test
	public void t3(){
		System.out.println(applicationContext.getBean("componentA1")); //me.ooi.demo.testspring43.beanname.ComponentA1@22635ba0
		System.out.println(applicationContext.getBean("componentA2")); //me.ooi.demo.testspring43.beanname.ComponentA2@13c10b87
	}
	
}
