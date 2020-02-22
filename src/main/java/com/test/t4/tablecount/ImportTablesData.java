package com.test.t4.tablecount;

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

public class ImportTablesData {

    public static final String mainUrl = "jdbc:mysql://10.69.0.207:3306/rightcloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
    public static final String mainUsername = "root";
    public static final String mainPw = "8mYupWnqiint7RATqKsbzOr50amprl";


    // 测试
    public static void main(String[] args) throws SQLException {

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        basicDataSource.setUrl(mainUrl);
        basicDataSource.setUsername(mainUsername);
        basicDataSource.setPassword(mainPw);
        Connection connection = basicDataSource.getConnection();

        List<String> tables = new LinkedList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            rs = dbMetaData.getTables(null, null, null, new String[]{"TABLE"});
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //close(rs, null, connection);
        }
        System.out.println("表数量：" + tables.size());

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
            System.out.println(
                    (i + 1) + " : " + resultList.get(i));
        }



    }
}
