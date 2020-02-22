package com.test.t0.itcaste;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.kugua.util.JdbcUtils;

 
public class PsTest {

	 
	public static void main(String[] args) throws SQLException {
		ps();
	}

	static void ps() throws SQLException {
		Connection conn = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			// 2.建立连接
			conn = JdbcUtils.getConnection();
			// conn = JdbcUtilsSing.getInstance().getConnection();
			// 3.创建语句

			String sql = "{ call addUser(?, ?, ?, ?) } ";
			cs = conn.prepareCall(sql);
			cs.registerOutParameter(4, Types.INTEGER);
			cs.setString(1, "ps name");
			cs.setDate(2, new java.sql.Date(System.currentTimeMillis()));
			cs.setFloat(3, 100f);

			cs.executeUpdate();

			int id = cs.getInt(4);

			System.out.println("id=" + id);
		} finally {
			JdbcUtils.close(rs, cs, conn);
		}
	}

}
