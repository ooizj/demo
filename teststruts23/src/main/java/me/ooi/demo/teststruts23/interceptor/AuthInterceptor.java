package me.ooi.demo.teststruts23.interceptor;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import me.ooi.demo.teststruts23.exception.AuthException;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class AuthInterceptor implements Interceptor {
	
	private static final long serialVersionUID = 1L;
	
	private String excludePattern ; 
	private Pattern excludeRegex ; 

	public String getExcludePattern() {
		return excludePattern;
	}

	public void setExcludePattern(String excludePattern) {
		this.excludePattern = excludePattern;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init() {
		excludeRegex = Pattern.compile(excludePattern) ; 
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		System.out.println("before");
		
		HttpServletRequest request = ServletActionContext.getRequest();
		
		boolean isNeedLoginUrl = true ; 

	    String url = request.getRequestURI() ; 
	    String contextPath = request.getContextPath() ; 
	    if( url.startsWith(contextPath) &&
	    		excludeRegex.matcher(url.substring(contextPath.length())).find() ){
	    	isNeedLoginUrl = false ; 
	    }
		
	    if( isNeedLoginUrl ){
	    	HttpSession session = request.getSession();
			if (session.getAttribute("user") == null) {
				throw new AuthException("请登录");
			}
	    }
		
		String ret = invocation.invoke();
		System.out.println("after");
		return ret;
	}
}
