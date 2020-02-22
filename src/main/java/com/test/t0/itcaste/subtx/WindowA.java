package com.test.t0.itcaste.subtx;

import com.kugua.util.JdbcUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

/**
 * @author Hong.Wu
 * @date: 9:54 2019/12/28
 */
public class WindowA {

    public static void main(String[] args) {
        Connection conn = null;
        Statement st = null;
        try {
            conn = JdbcUtils.getConnection();
            conn.setAutoCommit(false);

            st = conn.createStatement();
            String sql = "update account set money=money-10 where id=1";
            st.executeUpdate(sql);

            System.out.println("事务提交中.....转账");
            TimeUnit.SECONDS.sleep(10);

            conn.commit();
            System.out.println("转账完成");
        } catch (Exception e) {
            System.out.println("操作出错!");
            JdbcUtils.rollback(conn);
        } finally {
            JdbcUtils.free(null, st, conn);
        }
    }
}
