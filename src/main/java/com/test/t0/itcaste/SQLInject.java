package com.test.t0.itcaste;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.kugua.util.JdbcUtils;
 
 

 

public class SQLInject {
	
	
	
	public static void main(String[] args) throws SQLException {
		String name = "xxx' or 1=1 or '";
		query(name );
	}
 
	
	private static void query(String name) throws SQLException {
		Connection conn = JdbcUtils.getConnection(); 
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			// 查询总记录
			String sql = "select count(*) from users where username='"+name+"'";
			System.out.println(sql);
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet.next()) {
				System.out.println("total = "+resultSet.getInt(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		JdbcUtils.close(resultSet, statement, conn);
	}
	
	
}
