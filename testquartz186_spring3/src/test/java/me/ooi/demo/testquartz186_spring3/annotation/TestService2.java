package me.ooi.demo.testquartz186_spring3.annotation;

import org.springframework.stereotype.Service;

/**
 * @author jun.zhao
 */
@QuartzJob
@Service
public class TestService2 {
	
	@QuartzJobMethod(cron = "*/2 * * * * ?")
	public void exec() {
		System.out.println("execute-TestService2");
	}

}
