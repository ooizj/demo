package me.ooi.demo.testtransaction_bitronix;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestBitronixTransaction {
	
	private PoolingDataSource ds1 ; 
	private PoolingDataSource ds2 ; 
	
	@Before
	public void init(){
		ds1 = DataSourceUtils.createTestDataource1() ; 
		ds1.init();
		
		ds2 = DataSourceUtils.createTestDataource2() ; 
		ds2.init();
	}
	
	@After
	public void destroy(){
		ds1.close();
		ds2.close();
		
		TransactionManagerServices.getTransactionManager().shutdown();
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
		BitronixTransactionManager btm = TransactionManagerServices.getTransactionManager();
		try {
			btm.begin();
			
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

			btm.commit();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("rollback");
			btm.rollback();
		}
	}
	
	@Test
	public void t2() throws Exception{
		BitronixTransactionManager btm = TransactionManagerServices.getTransactionManager();
		try {
			btm.begin();
			
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
			System.out.println(con2); //different from above
//			con2.close();
			
			int a = 3/0 ; 

			btm.commit();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("rollback");
			btm.rollback();
		}
	}
	
	private void testGetConnectionTimeout(boolean closeConnection) throws Exception{
		BitronixTransactionManager btm = TransactionManagerServices.getTransactionManager();
		try {
			btm.begin();
			
			for (int i = 0; i < 21; i++) { // MaxPoolSize is 20 
				System.out.println("ds1------------------------------------------");
				Connection con1 = ds1.getConnection() ; 
				QueryUtils.insertUser(con1, "xiaoming");
				System.out.println(con1);
				if( closeConnection ){
					con1.close();
				}
				
				System.out.println("ds2------------------------------------------");
				Connection con2 = ds2.getConnection() ; 
				QueryUtils.insertUser(con2, "xiaoming2");
				System.out.println(con2);
				if( closeConnection ){
					con2.close();
				}
			}
			
			int a = 3/0 ; 

			btm.commit();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("rollback");
			btm.rollback();
		}
	}
	
	@Test
	public void t3() throws Exception{
		testGetConnectionTimeout(true); //no timeout
	}
	
	@Test
	public void t4() throws Exception{
		testGetConnectionTimeout(false); //timeout
	}
	
	@Test
	public void t5() throws Exception{
		final BitronixTransactionManager ut = TransactionManagerServices.getTransactionManager();
		
		final AtomicInteger counter = new AtomicInteger() ; 
		
		for (int i = 0; i < 21; i++) { // MaxPoolSize is 20 
			counter.incrementAndGet() ; 
			new Thread(()->{
				try {
					ut.begin();
					
					System.out.println(Thread.currentThread().getId()+ "------------------------------------------start");
					Connection con1 = ds1.getConnection() ; 
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
