package me.ooi.demo.testspring43_tx;


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
	
	public static void findAllUser2(Connection con) {
		try {
			findAllUser(con);
		} catch (SQLException e) {
			throw new RuntimeException(e) ; 
		}
	}
	
	public static void deleteAllUser(Connection con) throws SQLException {
		try(PreparedStatement ps = con.prepareStatement("delete from user")) {
			ps.executeUpdate() ; 
		}
	}
	
	public static void deleteAllUser2(Connection con) {
		try {
			deleteAllUser(con);
		} catch (SQLException e) {
			throw new RuntimeException(e) ; 
		}
	}
	
	public static void insertUser(Connection con, String userName) throws SQLException{
		try(PreparedStatement ps = con.prepareStatement("insert into user(name) "
				+ "values(?) ")) {
			ps.setString(1, userName) ; 
			ps.executeUpdate() ; 
		}
	}

	public static void insertUser2(Connection con, String userName) {
		try {
			insertUser(con, userName);
		} catch (SQLException e) {
			throw new RuntimeException(e) ; 
		}
	}
	
}
