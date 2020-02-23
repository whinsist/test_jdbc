package com.test.t4.tablecount;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.kugua.util.JdbcUtils;
import com.test.t3.difftable.CompareTable;
import com.test.t4.tablecount.vo.CountVo;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CountTables {

    public static final String mainUrl = "jdbc:mysql://10.69.0.207:3306/rightcloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
    public static final String mainUsername = "root";
    public static final String mainPw = "8mYupWnqiint7RATqKsbzOr50amprl";

//    public static final String mainUrl = "jdbc:mysql://192.168.93.132:3306/rightcloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
//    public static final String mainUsername = "root";
//    public static final String mainPw = "123456";


    // 测试
    public static void main(String[] args) throws SQLException {

//        test1();

        testUseDBCP();
        // testTooManyConnections();
    }


    private static void testUseDBCP() throws SQLException {

        List<String> tables = JdbcUtils.getTables(mainUrl, mainUsername, mainPw);
        System.out.println("表数量：" + tables.size());

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        basicDataSource.setUrl(mainUrl);
        basicDataSource.setUsername(mainUsername);
        basicDataSource.setPassword(mainPw);
        Connection connection = basicDataSource.getConnection();

        ResultSet rs = null;
        PreparedStatement ps = null;
        int totalCount = 0;
        List<CountVo> resultList = new ArrayList<>(tables.size());
        for (int i = 0; i < tables.size(); i++) {
            String sqlCount = "select count(1) from " + tables.get(i);
            ps = connection.prepareStatement(sqlCount);
            rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                totalCount += count;
                resultList.add(new CountVo(tables.get(i), count));
            }
        }
        System.out.println("表总记录数：" + totalCount);

        Collections.sort(resultList, (a, b) -> {
            return a.getCount() > b.getCount() ? -1 : (a.getCount().equals(b.getCount()) ? 0 : 1);
        });
        for (int i = 0; i < resultList.size(); i++) {
            System.out.println((i + 1) + " : " + resultList.get(i));
        }
        System.out.println(JSON.toJSONString(resultList));
    }

    private static void test1() throws SQLException {
        List<String> tableList = JdbcUtils.getTables(mainUrl, mainUsername, mainPw);
        Connection connection = JdbcUtils.getConnection(mainUrl, mainUsername, mainPw);
        for (int i = 0; i < tableList.size(); i++) {
            String sqlCount = "select count(1) from " + tableList.get(i);
            PreparedStatement ps = connection.prepareStatement(sqlCount);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(i + "---" + tableList.get(i) + "表    count:" + count);
            }
        }
        System.out.println(tableList);
    }

    private static void testTooManyConnections() throws SQLException {
        for (int i = 0; i < 500; i++) {
            Connection connection = JdbcUtils.getConnection(mainUrl, mainUsername, mainPw);
            PreparedStatement ps = connection.prepareStatement("select count(1) from license");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(i + "---" + count);
            }
            // 表太多的话 -->message from server: "Too many connections"
            // show variables like '%max_connection%';
            // 设置mysql最大连接数：set global max_connections=150;
            //JdbcUtils.free(rs, ps, connection);
        }
    }
}
