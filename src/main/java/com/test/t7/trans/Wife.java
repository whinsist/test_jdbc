package com.test.t7.trans;

import java.sql.*;

public class Wife {

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        try {
            //转账金额
            double money = 3000;
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://192.168.93.132:3306/test_jpa";
            connection = DriverManager.getConnection(url, "root", "123456");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String sql = "update account set balance=balance-" + money + " where card_id='6226090219290000'";
            statement.executeUpdate(sql);
            sql = "update account set balance=balance+" + money + " where card_id='6226090219299999'";
            statement.executeUpdate(sql);
            connection.commit();
            System.out.println("转账成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放资源
        }
    }
}