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
public class WindowB {

    public static void main(String[] args) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = JdbcUtils.getConnection();
            st = conn.createStatement();

            for (int i = 0; i < 1000; i++) {
                // 由于当前没开事务，如果WindowA一提交 此处是可以查询出减少了多少钱的
                rs = st.executeQuery("select * from account where id=1");
                while (rs.next()) {
                    System.out.println(
                            "第" + i + "查询 " + rs.getInt("id") + ":" + rs.getString("name") + "  " + rs.getFloat(
                                    "money"));
                }
                TimeUnit.SECONDS.sleep(1);
            }


        } catch (Exception e) {
            System.out.println("操作出错!");
        } finally {
            JdbcUtils.free(rs, st, conn);
        }
    }
}
