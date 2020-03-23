package me.ooi.demo.teststruts23.action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class HelloWorldAction extends ActionSupport{
	private static final long serialVersionUID = 1L;
	
	public static final Logger LOG = LogManager.getLogger(HelloWorldAction.class) ; 
	
	private String txt ;
	
	public String hello() {
		txt = "电波无法到达" ; 
		return SUCCESS ; 
	}

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}

}
