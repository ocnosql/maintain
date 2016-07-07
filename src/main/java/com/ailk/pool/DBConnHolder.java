package com.ailk.pool;

import java.sql.Connection;

import com.ailk.util.ConnUtil;

public class DBConnHolder implements ConnHolder{
	
	private String driver;
	private String url;
	private String username;
	private String password;
	
	private int dataBaseID;
	private String sysfile;
	
	private int status;
	
	public DBConnHolder(String driver, String url, String username,
			String password) {
		super();
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		status = 1;
	}
	
	public DBConnHolder(int dataBaseID, String sysfile){
		this.dataBaseID = dataBaseID;
		this.sysfile = sysfile;
		status = 2;
	}

	@Override
	public Object makeObject() throws Exception {
		if(status == 1)
			return ConnUtil.createConnection(driver, url, username, password);
		else
			return ConnUtil.createConnection(dataBaseID, sysfile);
	}

	@Override
	public void destroyObject(Object obj) throws Exception {
		// TODO Auto-generated method stub
		((Connection) obj).close();
	}

	@Override
	public boolean validateObject(Object obj) {
		boolean isConnect = true;
		if(obj == null){
			return false;
		}
		try{
			isConnect = !((Connection)obj).isClosed();
		}catch(Exception e){
			e.printStackTrace();
		}
		return isConnect;
	} 

	@Override
	public void activateObject(Object obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void passivateObject(Object obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
}
