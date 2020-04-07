package me.ooi.demo.testspring43.annotationaop;

import org.springframework.stereotype.Service;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class UserService {
	
	public int saveUser(String name){
		
//		System.out.println("生成的代理类："+AopContext.currentProxy().getClass());
//		AopContext.currentProxy().xxx();
		
		System.out.println("saveUser");
		return 1 ; 
	}
	
	public int updateUser(String name){
		System.out.println("updateUser");
		throw new RuntimeException("my test");
	}

}
