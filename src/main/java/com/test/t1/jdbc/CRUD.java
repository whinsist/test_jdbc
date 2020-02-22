package com.test.t1.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.kugua.util.JdbcUtils;
import com.mysql.jdbc.Statement;
  


public class CRUD {
	 
	public static void main(String[] args) throws SQLException {
		create();
//		update();
//		delete();
//		query();
	}

	

	private static void delete() {
		Connection connection = JdbcUtils.getConnection(); 
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("delete from users where id=1");
			int update = statement.executeUpdate();
			System.out.println(update);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, statement, connection);
		}
	}



	private static void update() {
		Connection connection = JdbcUtils.getConnection(); 
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("update users set password=123");
			int update = statement.executeUpdate();
			System.out.println(update);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, statement, connection);
		}
	}



	private static void create() {
		Connection connection = JdbcUtils.getConnection(); 
		PreparedStatement statement = null;
		Date date = new Date();
		java.sql.Date sdate = new java.sql.Date(date.getTime());
		try {
			statement = connection.prepareStatement("insert into users(username,password,birthday) values(?,?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, "test1");
			statement.setString(2, "test1");
			statement.setDate(3, sdate);
			int update = statement.executeUpdate();
			// 主键可能是string 也有可以是复合主键
			ResultSet resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				int uk = resultSet.getInt(1);
				System.out.println("id="+uk);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, statement, connection);
		}
	}
	
	private static void query() throws SQLException {
		Connection conn = JdbcUtils.getConnection(); 
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			// 查询总记录
			statement = conn.prepareStatement("select count(*) from users");
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				System.out.println("total = "+resultSet.getInt(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, statement, conn);
		}
	}
	
	
}
