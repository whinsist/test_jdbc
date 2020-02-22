package com.test.t0.itcaste;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.kugua.util.JdbcUtils;

/**
 * 
 * 2008-12-7
 * 
 * @author <a href="mailto:liyongibm@gmail.com">liyong</a>
 * 
 */
public class DBMD {

	/**
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		java.sql.Connection conn = JdbcUtils.getConnection();
		DatabaseMetaData dbmd = conn.getMetaData();
		System.out.println("db name: " + dbmd.getDatabaseProductName());
		System.out.println("tx: " + dbmd.supportsTransactions());
		conn.close();
	}

}
