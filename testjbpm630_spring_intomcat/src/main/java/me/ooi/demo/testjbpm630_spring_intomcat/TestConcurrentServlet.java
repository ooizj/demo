package me.ooi.demo.testjbpm630_spring_intomcat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet implementation class TestConcurrentServlet
 */
public class TestConcurrentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private TestConcurrentService testConcurrentService;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestConcurrentServlet() {
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
		testConcurrentService = context.getBean(TestConcurrentService.class);
		
		
		int count = 500;
		CountDownLatch cdl = new CountDownLatch(count);
		for (int i = 0; i < count; i++) {
			new Thread(()->{
				testConcurrentService.testWorkFLow();
				cdl.countDown();
			}).start();
		}
		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
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
