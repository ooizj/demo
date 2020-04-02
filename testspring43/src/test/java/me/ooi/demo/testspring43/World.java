package me.ooi.demo.testspring43;

import java.util.List;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class World {
	
	private List<User> users ;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "World [users=" + users + "]";
	} 

}
