package com.ailk.util;

import com.ailk.core.exception.ConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;


public class ConnUtil {
	
	public static final String DEFAULT_DRIVER = "oracle.jdbc.driver.OracleDriver";
	

	public static Connection createConnection(String driver, String url, String username, String password) throws ConnectionException{
		Connection conn = null;
		try{
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
			
		}catch(Exception e){
			throw new ConnectionException("创建数据库连接出现异常.", e);
		}
		return conn;
	}
	
	
	public static Connection createConnection(int dataBaseID, String sysfile, String driverClass){
		return null;
	}
	
	
	public static Connection createConnection(int dataBaseID, String sysfile){
		return createConnection(dataBaseID, sysfile, DEFAULT_DRIVER);
	}
	
	
	
	
	
}
