package me.ooi.demo.testspringboot213;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class MyTest1 {
	
	private int port = 10000;
	
	@Test
	public void t1() {
		assertEquals(port, 10000);
		
		TestRestTemplate restTemplate = new TestRestTemplate();
		assertThat(restTemplate.getForObject("http://localhost:" + port + "/testlock/testLock",
				String.class)).contains("this is a page");
	}
	
	@Test
	public void t2() throws InterruptedException {
		TestRestTemplate restTemplate = new TestRestTemplate();
		
		long st = System.currentTimeMillis() ;
		AtomicInteger count = new AtomicInteger(0) ;
		//100000000
		for (int i = 0; i < 10; i++) {
			count.incrementAndGet();
			new Thread(()->{

				try {
					System.out.println("hello-"+restTemplate.getForObject("http://localhost:" + port + "/testlock/testLock",
							String.class));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				count.decrementAndGet();
			}).start();
		}

		while( count.get() > 0 ){
			Thread.sleep(10);
		}
		
		long et = System.currentTimeMillis() ;
		System.out.println("耗时："+(et-st));

		Thread.sleep(1000);
	}

}
