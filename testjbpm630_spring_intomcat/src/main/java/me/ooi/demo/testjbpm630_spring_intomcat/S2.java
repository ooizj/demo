package me.ooi.demo.testjbpm630_spring_intomcat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class S2 {
	
	@Transactional
	public void t() {
		System.out.println("t");
		System.out.println(3/0);
	}

}
