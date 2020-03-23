package me.ooi.demo.teststruts25.testconventionaction;

import org.apache.struts2.convention.annotation.AllowedMethods;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author jun.zhao
 * @since 1.0
 */
@AllowedMethods({
	"list"
}) //除了使用注解，还可以使用“<allowed-methods>”、“<global-allowed-methods/>”
public class TestAllowAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	
	private String bookName ; 
	
	public String list(){
		bookName = "白夜行" ; 
		return "list" ; 
	}
	
	//由于此方法不在“@AllowedMethods”的列表中，所以调用会报错
	public String notAllowInvokeMethod(){
		bookName = "同级生" ; 
		return "list" ; 
	}

	//在严格模式下，此方法不允许被调用（除非加入到“@AllowedMethods”的列表中），struts2.5以前的版本没有严格模式，所以可以调用
	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	
}
