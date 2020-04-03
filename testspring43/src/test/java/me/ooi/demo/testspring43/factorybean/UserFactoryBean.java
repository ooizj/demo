package me.ooi.demo.testspring43.factorybean;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import me.ooi.demo.testspring43.User;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Scope("singleton")
@Component
public class UserFactoryBean implements FactoryBean<User>{

	@Override
	public User getObject() throws Exception {
		System.out.println("hhhh...");
		User u = new User() ; 
		u.setName("n1");
		return u ; 
	}

	@Override
	public Class<?> getObjectType() {
		return User.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
