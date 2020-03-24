package me.ooi.demo.testtransaction_atomikos;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AbstractDataSourceBean;
import com.atomikos.jdbc.AtomikosSQLException;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestAtomikosTransaction {
	
	private AbstractDataSourceBean ds1 ; 
	private AbstractDataSourceBean ds2 ; 
	
	private UserTransactionManager utm ; 
	
	@Before
	public void init() throws AtomikosSQLException, SystemException{
		ds1 = DataSourceUtils.createTestDataource1() ; 
		ds1.init();
		
		ds2 = DataSourceUtils.createTestDataource2() ; 
		ds2.init();
		
		utm = new UserTransactionManager() ; 
		utm.init();
	}
	
	@After
	public void destroy(){
		ds1.close();
		ds2.close();
		
		utm.close();
	}
	
	@Test
	public void deleteAll() throws Exception{
		System.out.println("ds1------------------------------------------");
		Connection con1 = ds1.getConnection() ; 
		QueryUtils.deleteAllUser(con1);
		con1.close();
		
		System.out.println("ds2------------------------------------------");
		Connection con2 = ds2.getConnection() ; 
		QueryUtils.deleteAllUser(con2);
		con2.close();
	}
	
	@Test
	public void findAll() throws Exception{
		System.out.println("ds1------------------------------------------");
		Connection con1 = ds1.getConnection() ; 
		QueryUtils.findAllUser(con1);
		con1.close();
		
		System.out.println("ds2------------------------------------------");
		Connection con2 = ds2.getConnection() ; 
		QueryUtils.findAllUser(con2);
		con2.close();
	}
	
	@Test
	public void t1() throws Exception{
		UserTransaction ut = new UserTransactionImp();
		try {
			ut.begin();
			
			System.out.println("ds1------------------------------------------");
			Connection con1 = ds1.getConnection() ; 
			QueryUtils.insertUser(con1, "xiaoming");
			System.out.println(con1);
			con1.close();
			
			System.out.println("ds1------------------------------------------");
			con1 = ds1.getConnection() ; 
			QueryUtils.findAllUser(con1);
			System.out.println(con1); //same as above
			con1.close();
			
			System.out.println("ds2------------------------------------------");
			Connection con2 = ds2.getConnection() ; 
			QueryUtils.insertUser(con2, "xiaoming2");
			System.out.println(con2);
			con2.close();
			
			System.out.println("ds2------------------------------------------");
			con2 = ds2.getConnection() ; 
			QueryUtils.findAllUser(con2);
			System.out.println(con2); //same as above
			con2.close();
			
			int a = 3/0 ; 

			ut.commit();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("rollback");
			ut.rollback();
		}
	}
	
	@Test
	public void t2() throws Exception{
		UserTransaction ut = new UserTransactionImp();
		try {
			ut.begin();
			
			System.out.println("ds1------------------------------------------");
			Connection con1 = ds1.getConnection() ; 
			QueryUtils.insertUser(con1, "xiaoming");
			System.out.println(con1);
//			con1.close();
			
			System.out.println("ds1------------------------------------------");
			con1 = ds1.getConnection() ; 
			QueryUtils.findAllUser(con1);
			System.out.println(con1); //different from above
//			con1.close();
			
			System.out.println("ds2------------------------------------------");
			Connection con2 = ds2.getConnection() ; 
			QueryUtils.insertUser(con2, "xiaoming2");
			System.out.println(con2);
//			con2.close();
			
			System.out.println("ds2------------------------------------------");
			con2 = ds2.getConnection() ; 
			QueryUtils.findAllUser(con2);
			System.out.println(con2); //same as above
//			con2.close();
			
			int a = 3/0 ; 

			ut.commit();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("rollback");
			ut.rollback();
		}
	}
	
	private void testGetConnectionTimeout(AbstractDataSourceBean ds, boolean closeConnection) throws Exception{
		UserTransaction ut = new UserTransactionImp();
		try {
			ut.begin();
			
			for (int i = 0; i < 21; i++) { // MaxPoolSize is 20 
				System.out.println("ds------------------------------------------");
				Connection con1 = ds.getConnection() ; 
				QueryUtils.insertUser(con1, "xiaoming");
				System.out.println(con1);
				if( closeConnection ){
					con1.close();
				}
			}
			
			int a = 3/0 ; 

			ut.commit();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("rollback");
			ut.rollback();
		}
	}
	
	@Test
	public void t3() throws Exception{
		testGetConnectionTimeout(ds1, true); //no timeout
	}
	
	@Test
	public void t3_2() throws Exception{
		testGetConnectionTimeout(ds2, true); //no timeout
	}
	
	@Test
	public void t4() throws Exception{
		//没有回收
		testGetConnectionTimeout(ds1, false); //timeout
	}
	
	@Test
	public void t4_2() throws Exception{
		testGetConnectionTimeout(ds2, false); //no timeout
	}
	
	@Test
	public void t5() throws Exception{
		UserTransaction ut = new UserTransactionImp();
		
		for (int i = 0; i < 21; i++) { // MaxPoolSize is 20 
			try {
				ut.begin();
				
				System.out.println("ds------------------------------------------");
				Connection con1 = ds2.getConnection() ; 
				QueryUtils.insertUser(con1, "xiaoming");
				System.out.println(con1); //all the same
				con1.close();
				
				int a = 3/0 ; 
	
				ut.commit();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("rollback");
				ut.rollback();
			}
		}
	}
	
	@Test
	public void t6() throws Exception{
		final UserTransaction ut = new UserTransactionImp();
		
		final AtomicInteger counter = new AtomicInteger() ; 
		
		for (int i = 0; i < 21; i++) { // MaxPoolSize is 20 
			counter.incrementAndGet() ; 
			new Thread(()->{
				try {
					ut.begin();
					
					System.out.println(Thread.currentThread().getId()+ "------------------------------------------start");
					Connection con1 = ds2.getConnection() ; 
					QueryUtils.insertUser(con1, "xiaoming");
					System.out.println(Thread.currentThread().getId()+ "\t"+con1); //有一样的，事物结束后可以重用
					con1.close();
					
//					int a = 3/0 ; 
		
					ut.commit();
					System.out.println(Thread.currentThread().getId()+ "------------------------------------------end\n");
					
					try { Thread.sleep(1000*3); } catch (Exception e1) {}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("rollback");
					try {
						ut.rollback();
						System.out.println(Thread.currentThread().getId()+ "------------------------------------------end\n");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					try { Thread.sleep(1000*3); } catch (Exception e1) {}
					
				} finally {
					counter.decrementAndGet() ; 
				}
			}).start(); 
		}
		
		while( counter.get() != 0 ){
			Thread.sleep(300);
		}
	}

}
