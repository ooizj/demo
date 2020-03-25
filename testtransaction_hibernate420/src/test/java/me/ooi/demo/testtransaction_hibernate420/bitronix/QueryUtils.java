package me.ooi.demo.testtransaction_hibernate420.bitronix;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class QueryUtils {
	
	public static void findAllUser(Connection con) throws SQLException{
		try(PreparedStatement ps = con.prepareStatement("select * from user ")) {
			try(ResultSet rs = ps.executeQuery()) {
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
			}
		}
	}
	
	public static void deleteAllUser(Connection con) throws SQLException {
		try(PreparedStatement ps = con.prepareStatement("delete from user")) {
			ps.executeUpdate() ; 
		}
	}
	
	public static void insertUser(Connection con, String userName) throws SQLException{
		try(PreparedStatement ps = con.prepareStatement("insert into user(name) "
				+ "values(?) ")) {
			ps.setString(1, userName) ; 
			ps.executeUpdate() ; 
		}
	}

}
