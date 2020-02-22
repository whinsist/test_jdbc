package com.test.t8.atomikos;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.kugua.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Properties;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * @author Hong.Wu
 * @date: 17:55 2020/02/22
 *
 *         https://www.bilibili.com/video/av57706540/?p=2
 */
public class AtomikosTest {


    public static void main(String[] args) {
        // 代理
        AtomikosDataSourceBean ds1 = createAtomikosDataSourceBean("testdb");
        AtomikosDataSourceBean ds2 = createAtomikosDataSourceBean("testdb2");

        Connection conn1 = null;
        Connection conn2 = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        UserTransaction userTransaction = new UserTransactionImp();

        try {
            userTransaction.begin();

            conn1 = ds1.getConnection();
            ps1 = conn1.prepareStatement("INSERT INTO article(author,content,title) VALUES (?, ?, ?); ",
                                         Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, "test");
            ps1.setString(2, "test");
            ps1.setString(3, "test");
            // 只是执行一下 不会真正提交
            ps1.executeUpdate();

             int i = 23/0;
            conn2 = ds2.getConnection();
            ps2 = conn2.prepareStatement("INSERT INTO message(name,content) VALUES (?, ?); ",
                                         Statement.RETURN_GENERATED_KEYS);
            ps2.setString(1, "test");
            ps2.setString(2, "test");
            ps2.executeUpdate();

            // 底层有完成的逻辑：重试、补偿、或写日志
            userTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                e1.printStackTrace();
            }
        } finally {
            JdbcUtils.close(null, ps1, conn1);
            JdbcUtils.close(null, ps2, conn2);
        }


    }

    private static AtomikosDataSourceBean createAtomikosDataSourceBean(String dbName) {
        Properties p = new Properties();
        p.setProperty("url", "jdbc:mysql://192.168.93.132:3306/" + dbName);
        p.setProperty("user", "root");
        p.setProperty("password", "123456");


        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName(dbName);
        ds.setXaDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
        ds.setXaProperties(p);
        return ds;
    }
}
