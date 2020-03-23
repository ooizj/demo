package me.ooi.demo.teststruts23.testconventionaction;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 测试convention插件<br>
 * 
 * className -> NameSpace 示例： <br>
 * com.example.actions.MainAction -> / <br>
 * com.example.actions.products.Display -> /products <br>
 * com.example.struts.company.details.ShowCompanyDetailsAction -> /company/details <br>
 * 
 * className -> 访问地址 示例： <br>
 * com.example.actions.MainAction -> /main <br>
 * com.example.actions.products.Display -> /products/display <br>
 * com.example.struts.company.details.ShowCompanyDetailsAction -> /company/details/show-company-details <br>
 * 
 * 返回结果对应页面规则： <br>
 * WEB-INF/content/NameSpace/Action名称首字母小写，其他大写字母变小写并在前面加“-”，加上“返回值”，加上“.jsp” <br>
 * 
 * BookAction 访问地址与返回页面路径： <br>
 * 	/book!list.action	WEB-INF/content/book-list.jsp <br>
 * 
 * @author jun.zhao
 * @since 1.0
 */
public class BookAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	
	private String bookName ; 
	
	public String list(){
		bookName = "白夜行" ; 
		return "list" ; 
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	
}
