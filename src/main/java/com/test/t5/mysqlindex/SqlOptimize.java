package com.test.t5.mysqlindex;

import com.kugua.util.Config;
import com.kugua.util.JdbcUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hong.Wu
 * @date: 9:04 2019/11/30
 */
public class SqlOptimize {

    // 测试
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String mainUrl = "jdbc:mysql://192.168.93.132:3306/babytun?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
        String mainUsername = "root";
        String mainPw = "123456";
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(mainUrl, mainUsername, mainPw);
        String sql = "select gc.* from t_goods g, t_goods_cover gc where g.goods_id=gc.goods_id  and g.category_id=44;";
        PreparedStatement ps = connection.prepareStatement(sql);
        int execCount = 100;
        long start = System.currentTimeMillis();
        for (int i = 0; i < execCount; i++) {
            ResultSet rs = ps.executeQuery();
            rs.close();
        }
        long end = System.currentTimeMillis();
        System.out.println(execCount+"次执行时间：" + (end - start) + "毫秒  平均执行时间：" + ((end - start) / execCount) + "毫秒");
    }
}
