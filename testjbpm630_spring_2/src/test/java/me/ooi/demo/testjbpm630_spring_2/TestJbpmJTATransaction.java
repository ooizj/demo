package me.ooi.demo.testjbpm630_spring_2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.spring.factorybeans.KSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application-context-jta.xml")
public class TestJbpmJTATransaction {
	
	@Autowired
	private ApplicationContext ctx;
	
	@Test
	public void t1() {
		KSessionFactoryBean
		System.out.println(ctx.getBean("ksession1"));
	}

}
