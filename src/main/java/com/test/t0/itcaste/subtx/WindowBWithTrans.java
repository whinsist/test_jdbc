package com.test.t0.itcaste.subtx;

import com.kugua.util.JdbcUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

/**
 * @author Hong.Wu
 * @date: 9:54 2019/12/28
 */
public class WindowBWithTrans {

    public static void main(String[] args) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = JdbcUtils.getConnection();
            st = conn.createStatement();
            conn.setAutoCommit(false);
            //conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            for (int i = 0; i < 1000; i++) {
                // 由于当前开起了事务，mysql的默认隔离级别是可以重复读，所以money不会变化，
                // 如果想看到变化效果可以改成不开事务，或者 conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                rs = st.executeQuery("select * from account where id=1");
                while (rs.next()) {
                    System.out.println(
                            "第" + i + "查询 " + rs.getInt("id") + ":" + rs.getString("name") + "  " + rs.getFloat(
                                    "money"));
                }
                TimeUnit.SECONDS.sleep(1);
            }

            conn.commit();

        } catch (Exception e) {
            System.out.println("操作出错!");
        } finally {
            JdbcUtils.free(rs, st, conn);
        }
    }
}
