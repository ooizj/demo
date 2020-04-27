package me.ooi.demo.testspring43_tx.jdbc;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import me.ooi.demo.testspring43_tx.QueryUtils;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class UserService {
	
	@Autowired
	private DataSource dataSource ; 
	
	public int deleteAllUser() {
		Connection con = DataSourceUtils.getConnection(dataSource) ;
		QueryUtils.deleteAllUser2(con);
		return 1 ; 
	}
	
	public int findAllUser() {
		Connection con = DataSourceUtils.getConnection(dataSource) ;
		QueryUtils.findAllUser2(con);
		return 1 ; 
	}
	
	@Transactional
	public int saveUser(String name){
		Connection con = DataSourceUtils.getConnection(dataSource) ;
		QueryUtils.insertUser2(con, name);
		return 1 ; 
	}
	
	@Transactional
	public int saveUser2(String name){
		Connection con = DataSourceUtils.getConnection(dataSource) ;
		QueryUtils.insertUser2(con, name);
		System.out.println(3/0);
		return 1 ; 
	}
	
	@Transactional
	public int testRegisterSynchronization(String name){
		
		// register synchronisation
	    if(TransactionSynchronizationManager.isActualTransactionActive()) {
	    	TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override
				public void beforeCommit(boolean readOnly) { //报错影响事务提交，异常会抛出去
					System.out.println("beforeCommit");
				}
				@Override
				public void beforeCompletion() { //报错不影响事务提交，程序继续往下走，事务内代码异常也执行
					System.out.println("beforeCompletion");
				}
				@Override
				public void afterCommit() { //报错不影响事务提交，异常会抛出去
					System.out.println("afterCommit");
				}
				@Override
				public void afterCompletion(int status) { //报错不影响事务提交，程序继续往下走，事务内代码异常也执行
					System.out.println("afterCompletion "+status);
//					int a = 3/0;
				}
			});
	    }
		
		Connection con = DataSourceUtils.getConnection(dataSource) ;
		QueryUtils.insertUser2(con, name);
		
//		int a = 3/0;
		
		return 1 ; 
	}
	
}
