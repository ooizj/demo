package me.ooi.demo.testquartz186_spring3.xmlandannotation;

import org.springframework.stereotype.Service;

import me.ooi.demo.testquartz186_spring3.annotation.QuartzJob;
import me.ooi.demo.testquartz186_spring3.annotation.QuartzJobMethod;

/**
 * @author jun.zhao
 */
@QuartzJob
@Service
public class TestService4 {
	
	@QuartzJobMethod(cron = "*/1 * * * * ?")
	public void exec() {
		System.out.println("execute-TestService4");
	}

}
