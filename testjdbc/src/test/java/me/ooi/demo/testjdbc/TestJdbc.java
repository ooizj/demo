package me.ooi.demo.testjdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author jun.zhao
 */
public class TestJdbc {
	
	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost/mytest?useUnicode=true&characterEncoding=utf8&autoReconnect=true&useOldAliasMetadataBehavior=true&useSSL=false", 
				"root", 
				"root");
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
	public void getTableInfo() throws SQLException, ClassNotFoundException {
		Connection con = getConnection() ; 
//		oraCon.setRemarksReporting(true);
		
		DatabaseMetaData dsMetaData = con.getMetaData() ; 
		for (String tableName : new String[] {"user", "depart"}) {
			System.out.println("-------------------------------------"+tableName);
			ResultSet rs = dsMetaData.getColumns(null, null, tableName, null) ; 
			
			ResultSetMetaData meta = rs.getMetaData() ; 
	    	int columnCount = meta.getColumnCount();
			
			while( rs.next() ) {
//				for (int j = 0; j < columnCount; j++) {
//					System.out.print(meta.getColumnTypeName(j+1)+":");
//					System.out.print(rs.getObject(j+1)+",");
//				}
//				System.out.println();
				
				String columnName = rs.getString("COLUMN_NAME");
			    String datatype = rs.getString("DATA_TYPE");
			    String columnsize = rs.getString("COLUMN_SIZE");
			    String decimaldigits = rs.getString("DECIMAL_DIGITS");
			    String isNullable = rs.getString("IS_NULLABLE");
			    String is_autoIncrment = rs.getString("IS_AUTOINCREMENT");
//			    System.out.println(meta.getColumnClassName(column));
			    //Printing results
			    System.out.println(rs.getString("REMARKS"));
			    System.out.println(columnName + "---" + datatype + "---" + columnsize + "---" + decimaldigits + "---" + isNullable + "---" + is_autoIncrment);
			}
		}
		con.close();
	}
	
	@Test
	public void getSelectResultInfo() throws SQLException, ClassNotFoundException{
		Connection con = getConnection() ; 
		PreparedStatement ps = con.prepareStatement("select * from user ") ; 
		ResultSet rs = ps.executeQuery() ;
		
		ResultSetMetaData meta = rs.getMetaData() ; 
    	int columnCount = meta.getColumnCount();
    	for (int i = 1; i <= columnCount; i++) {
    		System.out.println("--------------------------------------------------");
        	System.out.println("getColumnName="+meta.getColumnName(i));
        	System.out.println("getColumnLabel="+meta.getColumnLabel(i));
        	System.out.println("getColumnTypeName="+meta.getColumnTypeName(i));
        	System.out.println("getColumnClassName="+meta.getColumnClassName(i));
        }
		
		rs.close();
		ps.close();
		con.close();
	}
	
	public static Map<String, Class<?>> getTestFieldMap1() throws SQLException, ClassNotFoundException{
		
		Map<String, Class<?>> fieldMap = new LinkedHashMap<String, Class<?>>();
		
		String sql = "select * from user";
		Connection con = getConnection() ; 
		PreparedStatement ps = con.prepareStatement(sql) ; 
		ResultSet rs = ps.executeQuery() ;
		
		ResultSetMetaData meta = rs.getMetaData() ; 
    	int columnCount = meta.getColumnCount();
    	for (int i = 1; i <= columnCount; i++) {
        	fieldMap.put(meta.getColumnName(i), Class.forName(meta.getColumnClassName(i)));
        }
		
		rs.close();
		ps.close();
		con.close();
		
		
		return fieldMap;
	}
	
	@Test
	public void testGetTestFieldMap1() throws ClassNotFoundException, SQLException {
		System.out.println(getTestFieldMap1());
	}

}
