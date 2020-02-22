package com.test.t1.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.kugua.util.JdbcUtils;

public class TestBatchData {

	public static void main(String[] args) throws SQLException {
		//exec1();
		//exec2();
		exec3();
	}

	// 3.使用PreparedStatement + 批处理 
	public static void exec3() {
		int total = 101;
		int everyCommitCount = 3;
		try {
			Connection conn = JdbcUtils.getConnection();
			conn.setAutoCommit(false);
			Long beginTime = System.currentTimeMillis();
			PreparedStatement pst = conn.prepareStatement("insert into population(name,city,birthday) values (?,?,?)");
			for (int i = 1; i <= total; i++) {
				pst.setString(1, "name"+i);
				pst.setString(2, "city"+i);
				pst.setDate(3, new Date(new java.util.Date().getTime()));
				pst.addBatch();

				// 可以设置不同的大小；如50，100，500，1000等等
				if (i % everyCommitCount == 0) {
					int[] executeBatch = pst.executeBatch();
					conn.commit();
					pst.clearBatch();
					System.out.println(i);
				}
			}
			// 提交剩余的数据
			if (everyCommitCount % total != 0) {
				pst.executeBatch();
				conn.commit();
			}
			Long endTime = System.currentTimeMillis();
			System.out.println("pst+batch：" + (endTime - beginTime) / 1000 + "秒");
			pst.close();
			conn.close();
		} catch (SQLException e) { 
			e.printStackTrace();
		}
	}

	// 2.使用PreparedStatement对象   5秒
	public static void exec2() {
		try {
			Long beginTime = System.currentTimeMillis();
			Connection conn = JdbcUtils.getConnection();
			conn.setAutoCommit(false);// 手动提交
			PreparedStatement pst = conn.prepareStatement("insert into t1(name) values (?)");
			for (int i = 0; i < 100000; i++) {
				pst.setString(1, "pst_"+i);
				pst.execute();
			}
			conn.commit();
			Long endTime = System.currentTimeMillis();
			System.out.println("pst:" + (endTime - beginTime) / 1000 + "秒");// 计算时间
			pst.close();
			conn.close();
		} catch (SQLException e) { 
			e.printStackTrace();
		}
	}

	// 1.使用statement插入100000条记录
	public static void exec1() {
		try {
			Long beginTime = System.currentTimeMillis();
			Connection conn = JdbcUtils.getConnection();
			conn.setAutoCommit(false);// 设置手动提交
			Statement st = conn.createStatement();
			for (int i = 0; i < 1000000; i++) {
				String sql = "insert into t1(name) values ('st_" + i + "')";
				st.executeUpdate(sql);
			}
			Long endTime = System.currentTimeMillis();
			System.out.println("st：" + (endTime - beginTime) / 1000 + "秒");// 计算时间

			conn.commit();
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
