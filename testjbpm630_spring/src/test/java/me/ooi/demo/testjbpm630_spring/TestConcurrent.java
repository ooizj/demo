package me.ooi.demo.testjbpm630_spring;

import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestConcurrent {
	
	private ClassPathXmlApplicationContext ctx ; 
	
	private TestConcurrentService testConcurrentService;
	
	@Before
	public void before(){
		ctx = new ClassPathXmlApplicationContext("application-context.xml") ; 
		testConcurrentService = ctx.getBean(TestConcurrentService.class);
	}
	
	@After
	public void after(){
		ctx.close();
	}
	
	@Test
	public void t1(){
		testConcurrentService.testWorkFLow();
	}
	
	@Test
	public void t2() throws InterruptedException{
		int count = 500;
		CountDownLatch cdl = new CountDownLatch(count);
		for (int i = 0; i < count; i++) {
			new Thread(()->{
				testConcurrentService.testWorkFLow();
				cdl.countDown();
			}).start();
		}
		cdl.await();
	}

}
