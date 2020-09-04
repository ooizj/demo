package me.ooi.demo.testactiviti710_springboot;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestActiviti710SpringBoot3 {
	
    @Autowired
    private TestActivitiService testActivitiService;
    
    @Test
    public void t1() {
    	testActivitiService.t1();
    }
    
    @Test
    public void t2() throws InterruptedException {
    	int count = 1;
    	CountDownLatch cdl = new CountDownLatch(count);
    	for (int i = 0; i < count; i++) {
			new Thread(()->{
				try {
					testActivitiService.t2();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cdl.countDown();
				}
			}).start();
		}
    	cdl.await();
    }
    
}