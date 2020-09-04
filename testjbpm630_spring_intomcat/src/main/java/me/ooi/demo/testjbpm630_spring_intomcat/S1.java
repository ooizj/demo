package me.ooi.demo.testjbpm630_spring_intomcat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class S1 {
	
	@Autowired
	private S2 s2;
	
	//test: transaction was marked as rollback only and has been rolled back
	@Transactional
	public void t() {
		try {
			s2.t();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
