package com.test.t7.trans;

import java.sql.*;

/**
 * https://blog.csdn.net/YXX_decsdn/article/details/91347114
 */
public class Machine {

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            //消费金额
            double sum = 1000;
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://192.168.93.132:3306/test_jpa";
            connection = DriverManager.getConnection(url, "root", "123456");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String sql = "select balance from account where card_id='6226090219290000'";
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                System.out.println("余额：" + resultSet.getDouble("balance"));
            }

            System.out.println("请输入支付密码：");
            //20秒后密码输入成功
            Thread.sleep(20000);

            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                System.out.println("余额：" + balance);
                if (balance < sum) {
                    System.out.println("余额不足，扣款失败！");
                    return;
                }
            }

            sql = "update account set balance=balance-" + sum + " where card_id='6226090219290000'";
            statement.executeUpdate(sql);
            connection.commit();
            System.out.println("扣款成功！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放资源
        }
    }
}