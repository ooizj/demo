package me.ooi.demo.testtransaction_jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.Test;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestJdbcTransaction {
	
	private Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost/mytest?useUnicode=true&characterEncoding=utf8&autoReconnect=true&useOldAliasMetadataBehavior=true&useSSL=false", "root", "root");
		return conn ; 
	}
	
	@Test
	public void findAllUser() throws SQLException, ClassNotFoundException{
		Connection con = getConnection() ; 
		PreparedStatement ps = con.prepareStatement("select * from user ") ; 
		ResultSet rs = ps.executeQuery() ;
		ResultSetMetaData metaData = rs.getMetaData() ; 
		int columnCount = metaData.getColumnCount() ; 
		boolean hasRecord = false ; 
		while( rs.next() ){
			hasRecord = true ; 
			for (int i = 1; i <= columnCount; i++) {
				Object obj = rs.getObject(i) ;
				System.out.println(metaData.getColumnName(i) +"\t"+ obj);
			}
			System.out.println("--------------------------------");
		}
		if( !hasRecord ){
			System.out.println("no data");
		}
		rs.close();
		ps.close();
		con.close();
	}
	
	@Test
	public void deleteAllUser() throws SQLException, ClassNotFoundException{
		Connection con = getConnection() ; 
		PreparedStatement ps = con.prepareStatement("delete from user ") ; 
		ps.executeUpdate() ; 
		ps.close();
		con.close();
	}
	
	@Test
	public void testTransaction() throws ClassNotFoundException, SQLException{
		Connection con = getConnection() ; 
		boolean autoCommit = con.getAutoCommit() ; 
		try {
			
			if( autoCommit ){
				con.setAutoCommit(false); 
			}
			
			try(PreparedStatement ps = con.prepareStatement("insert into user(name) "
					+ "values(?) ")) {
				ps.setString(1, "xiaoming") ; 
				ps.executeUpdate() ; 
			}
			
//			int a = 3/0;
			
			con.commit(); 
			
		} catch (Exception e) {
			e.printStackTrace();
			con.rollback(); 
		} finally {
			con.setAutoCommit(autoCommit) ;
			con.close();
		}
		
	}

}
