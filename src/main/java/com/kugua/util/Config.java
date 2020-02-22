package com.kugua.util;

import java.io.IOException;
import java.util.Properties;

 

public class Config {
	private static Properties properties = new Properties();
	static{ 
		try {
			// 在当前类的包下
			//properties.load(Config.class.getResourceAsStream("jdbc.properties"));
			// 在resources下或src/main/java下
			properties.load(Config.class.getClassLoader().getResourceAsStream("jdbc.properties"));
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	public static final String DRIVER = properties.getProperty("driver");
	public static final String URL = properties.getProperty("url");
	public static final String USERNAME = properties.getProperty("username");
	public static final String PASSWORD = properties.getProperty("password"); 
}
