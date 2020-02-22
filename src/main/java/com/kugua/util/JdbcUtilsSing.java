package com.kugua.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

 

 
public final class JdbcUtilsSing {
	
	//private static JdbcUtilsSing instance = new JdbcUtilsSing();
	private static JdbcUtilsSing instance = null;
	
	private JdbcUtilsSing() {
	}
	
	
	public JdbcUtilsSing getInstance() {
		// 懒加载方式
		if (instance == null) {
			synchronized (JdbcUtilsSing.class) {
				// 必须双重检查  多线程时 都同时为instance==null中， 如果synchronized写在方法上不划算 这个只会锁第一次
				if (instance == null) { 
					instance = new JdbcUtilsSing();
				}
			}
		}
		return instance;
	}
	
	
	
	static{
	}
	
	public  Connection getConnection(){
		return getConnection(Config.URL+"?useUnicode=true&characterEncoding=UTF-8", Config.USERNAME, Config.PASSWORD);
	}
	public  Connection getConnection(String url, String user, String password){
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) { 
			e.printStackTrace();
			return null;
		}
	}
	
	 
	
}
