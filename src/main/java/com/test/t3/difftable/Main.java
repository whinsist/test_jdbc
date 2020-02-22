package com.test.t3.difftable;

import java.sql.SQLException;
import java.util.List;

import com.kugua.util.JdbcUtils;

public class Main {

    public static final String mainUrl = "jdbc:mysql://10.69.0.207:3306/rightcloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
    public static final String mainUsername = "root";
    public static final String mainPw = "8mYupWnqiint7RATqKsbzOr50amprl";

    public static final String otherUrl = "jdbc:mysql://192.168.93.132:3306/rightcloud_gky";
    public static final String otherUsername = "root";
    public static final String otherPw = "123456";

    public static final String compareSQL = "e:/temp/deff.sql";

    // 测试
    public static void main(String[] args) throws SQLException {

        List<String> tableList = JdbcUtils.getTables(mainUrl, mainUsername, mainPw);
        System.out.println(tableList);
        CompareTable test = new CompareTable(mainUrl, mainUsername, mainPw, otherUrl, otherUsername, otherPw,
                                             compareSQL);
        test.match(tableList);
    }
}
