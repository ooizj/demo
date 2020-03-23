package me.ooi.demo.teststruts23.testconventionaction.author;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class AuthorSearchAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	
	private String name ; 
	
	public String list(){
		name = "东野圭吾" ; 
		return "list" ; 
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
