package me.ooi.demo.testspring43.annotationbean;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import me.ooi.demo.testspring43.User;
import me.ooi.demo.testspring43.World;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Configuration
public class TestConfiguration {
	
	@Autowired
	@Qualifier("u1")
	private User u1 ; 
	
	@Autowired
	@Qualifier("u1")
	private User u2 ; 
	
	@Autowired
	@Qualifier("u0")
	private User u01 ; 
	
	@Autowired
	@Qualifier("u0")
	private User u02 ; 
	
	@Scope("prototype")
	@Bean
	public User u1(){
		User u = new User() ; 
		u.setName("u1");
		return u ; 
	}
	
	@Scope("singleton")
	@Bean
	public User u0(){
		User u = new User() ; 
		u.setName("u1");
		return u ; 
	}

	@Scope("singleton")
	@Bean(name="w")
	public World w(){
		World w = new World() ; 
		w.setUsers(new ArrayList<>());
		w.getUsers().add(u1) ; 
		w.getUsers().add(u2) ; 
		w.getUsers().add(u01) ; 
		w.getUsers().add(u02) ; 
		return w ; 
	}
	
}
