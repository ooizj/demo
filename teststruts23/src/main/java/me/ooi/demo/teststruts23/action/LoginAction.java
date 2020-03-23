package me.ooi.demo.teststruts23.action;

import com.opensymphony.xwork2.ActionSupport;

import me.ooi.demo.teststruts23.action.vo.LoginUser;
import me.ooi.demo.teststruts23.exception.AuthException;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class LoginAction extends ActionSupport{
	private static final long serialVersionUID = 1L;
	
	private LoginUser loginUser ; 
	
	public String test(){
		return SUCCESS ; 
	}
	
	public String toLoginPage(){
		return "login" ; //test global result
	}
	
	public String login(){
		if( !"root".equals(loginUser.getUsername()) ){
			throw new AuthException("用户名或密码错误！") ; 
		}else {
			return SUCCESS ; 
		}
	}

	public LoginUser getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(LoginUser loginUser) {
		this.loginUser = loginUser;
	}

}
