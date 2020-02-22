package com.test.t0.itcaste;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * 2008-12-6
 * 
 * @author <a href="mailto:liyongibm@gmail.com">liyong</a>
 * 
 */
public class DaoFactory {
	
	//userDao必须定义在上面 不然会先执行该构造方法，又覆盖userDao为空
	//private static UserDao userDao = null;
	private static DaoFactory instance = new DaoFactory();

	private DaoFactory() {
		try {
			Properties prop = new Properties();
			InputStream inStream = DaoFactory.class.getClassLoader().getResourceAsStream("daoconfig.properties");
			prop.load(inStream);
			String userDaoClass = prop.getProperty("userDaoClass");
			Class clazz = Class.forName(userDaoClass);
			//userDao = (UserDao) clazz.newInstance();
		} catch (Throwable e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static DaoFactory getInstance() {
		return instance;
	}

	//public UserDao getUserDao() {
	//	return userDao;
	//}
}
