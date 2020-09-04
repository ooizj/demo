package me.ooi.demo.testjbpm630_spring_intomcat;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet implementation class TestTransactionServlet
 */
public class TestTransactionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private TestTransactionService testTransactionService;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestTransactionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ApplicationContext context = 
			    WebApplicationContextUtils.getRequiredWebApplicationContext(
			        this.getServletContext());
		testTransactionService = context.getBean(TestTransactionService.class);
		
//		testTransactionService.testTransaction2();
		testTransactionService.testWorkFLow();
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
