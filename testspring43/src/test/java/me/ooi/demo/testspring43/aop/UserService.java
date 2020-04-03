package me.ooi.demo.testspring43.aop;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class UserService {
	
	public int saveUser(String name){
		System.out.println("saveUser");
		return 1 ; 
	}
	
	public int updateUser(String name){
		System.out.println("updateUser");
		throw new RuntimeException("my test");
	}

}
