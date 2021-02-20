package me.ooi.demo.testquartz186_cluster_spring3;

import static me.ooi.demo.testquartz186_cluster_spring3.TestUtils.sleep;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author jun.zhao
 */
public class TestQuartzWithSpringXmlConfig {
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("/application-context.xml");
		
		sleep(1000*15);
		ctx.close();
	}
	
}
