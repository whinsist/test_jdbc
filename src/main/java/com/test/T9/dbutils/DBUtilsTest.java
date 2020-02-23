package com.test.T9.dbutils;

import com.alibaba.druid.pool.DruidDataSource;
import com.kugua.util.JdbcUtils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Hong.Wu
 * @date: 23:10 2020/02/22
 */
public class DBUtilsTest {

    public static final String mainUrl = "jdbc:mysql://192.168.93.132:3306/rightcloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
    public static final String mainUsername = "root";
    public static final String mainPw = "123456";

    public static void main(String[] args) throws Exception {

        testCompare();

//        QueryRunner runner = new QueryRunner();
//        Connection conn = JdbcUtils.getConnection(mainUrl, mainUsername, mainPw);
//        String sql = "INSERT INTO message(name,content) VALUES (?, ?)";
//        Long id = runner.insert(conn, sql, new ScalarHandler<Long>(), "myself", "myself");
//
//
//        DruidDataSource druidDataSource = new DruidDataSource();
//        druidDataSource.setUrl(mainUrl);
//        druidDataSource.setUsername(mainUsername);
//        druidDataSource.setPassword(mainPw);
//        druidDataSource.setTestOnBorrow(false);
//        druidDataSource.setTestOnReturn(false);
//        druidDataSource.setTestWhileIdle(true);
//        druidDataSource.setValidationQuery("select 'x'");
//        druidDataSource.setMaxActive(20);
//        druidDataSource.setMinIdle(1);
//        druidDataSource.setPoolPreparedStatements(false);
//        druidDataSource.setTimeBetweenEvictionRunsMillis(60 * 1000);
//        druidDataSource.setMinEvictableIdleTimeMillis(300000);
//        druidDataSource.setRemoveAbandoned(true);
//        druidDataSource.setRemoveAbandonedTimeout(3600);
//        QueryRunner runnerPool = new QueryRunner(druidDataSource);
//        ResultSetHandler<String> resultHandler = new BeanHandler<>(String.class);
//        String genKey = runnerPool.insert(sql, resultHandler, "aaa", "bbb");

    }

    private static void testCompare() throws Exception {
        List<String> tableList = JdbcUtils.getTables(mainUrl, mainUsername, mainPw);

        long time1 = System.currentTimeMillis();
        QueryRunner runner = new QueryRunner();
        Connection conn = JdbcUtils.getConnection(mainUrl, mainUsername, mainPw);
        for (int i = 0; i < tableList.size(); i++) {
            String sql = "select count(1) as count from " + tableList.get(i);
            //List<Integer> result = runner.query(conn, sql, new BeanListHandler<>(Integer.class));
            Map result = runner.query(conn, sql, new MapHandler());
            //System.out.println(i + "---" + tableList.get(i) + "表    count:" + result.get("count"));
        }
        long time2 = System.currentTimeMillis();
        System.out.println("------------------" + (time2 - time1));

        long time3 = System.currentTimeMillis();

        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(mainUrl);
        druidDataSource.setUsername(mainUsername);
        druidDataSource.setPassword(mainPw);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setValidationQuery("select 'x'");
        druidDataSource.setMaxActive(20);
        druidDataSource.setMinIdle(1);
        druidDataSource.setPoolPreparedStatements(false);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60 * 1000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(3600);
        QueryRunner druiRrunner = new QueryRunner(druidDataSource);

        for (int i = 0; i < tableList.size(); i++) {
            String sql = "select count(1) as count from " + tableList.get(i);
            Map result = druiRrunner.query(sql, new MapHandler());
            //System.out.println(i + "---" + tableList.get(i) + "表    count:" + result.get("count"));
        }
        long time4 = System.currentTimeMillis();
        System.out.println("------------------" + (time4 - time3));

    }
}
