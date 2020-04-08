package me.ooi.demo.testspring43_tx.jdbc;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
}
