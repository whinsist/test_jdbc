package com.test.t4.tablecount;

import com.kugua.util.Canstants;
import com.kugua.util.JdbcUtils;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ImportTablesData {

    private static final List<String> excludeTablePrefix = Arrays.asList("act_", "qrtz_", "cloud_exa_");
    private static final List<String> excludeTables = Arrays.asList("res_vpc_port", "instance_gaap_cost", "deploy_task",
                                                                    "sys_t_msg", "cloud_exa_report_detail",
                                                                    "res_action_log",
                                                                    "server_template");
    // 手动传输表：act_XXX qrtz_XXX server_template

    private static final String FILE_PATH = "e:/temp/importSuccessTable.txt";
    private static final String BANK_STR = "           ";


//    private static final String mainUrl = "jdbc:mysql://10.69.0.207:3306/rightcloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
//    private static final String mainUsername = "root";
//    private static final String mainPw = "8mYupWnqiint7RATqKsbzOr50amprl";


    private static final String mainUrl = "jdbc:mysql://10.69.0.51:45859/rightcloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
    private static final String mainUsername = "root";
    private static final String mainPw = "1q2w3e4r";


    private static final String innerUrl = "jdbc:mysql://192.168.93.132:3308/rightcloud_maas" + Canstants.MYSQL_URL_END;
    private static final String innerUsername = "root";
    private static final String innerPw = "123456";


    // 测试
    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        basicDataSource.setUrl(mainUrl);
        basicDataSource.setUsername(mainUsername);
        basicDataSource.setPassword(mainPw);
        Connection connection = basicDataSource.getConnection();
        List<String> tableList = getNeedImportTables();

        Connection connection2 = JdbcUtils.getConnection(innerUrl, innerUsername, innerPw);

        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;

        int counter = 0;
        for (String table : tableList) {
            long time1 = System.currentTimeMillis();
            counter++;
            int totalRows = getTableCount(connection, table);
            System.out.println(counter + "/" + tableList.size() + " -- " + table + ":" + totalRows);

            try {
                // 获取表所有字段及总数
                List<String> fields = getFields(connection, table);
                String insertSQL = getInsertSQL(table, fields);

                ps2 = connection2.prepareStatement("truncate table " + table);
                ps2.executeUpdate();
                JdbcUtils.close(null, ps2, null);

                ps = connection.prepareStatement("select * from " + table);
                rs = ps.executeQuery();

                ////////重新开一个Connection进行批量提交
                int everyCommitCount = 3000;
                Connection connBatch = JdbcUtils.getConnection(innerUrl, innerUsername, innerPw);
                connBatch.setAutoCommit(false);
                PreparedStatement psBatch = connBatch.prepareStatement(insertSQL);
                int batchCounter = 0;
                while (rs.next()) {
                    batchCounter++;
                    for (int f = 0; f < fields.size(); f++) {
                        psBatch.setObject(f + 1, rs.getObject(fields.get(f)));
                    }
                    psBatch.addBatch();
                    if (batchCounter % everyCommitCount == 0) {
                        psBatch.executeBatch();
                        connBatch.commit();
                        psBatch.clearBatch();
                        System.out.println(BANK_STR + table + " : " + batchCounter + "/" + totalRows);
                    }
                }
                // 提交剩余的数据
                psBatch.executeBatch();
                connBatch.commit();
                JdbcUtils.free(null, psBatch, connBatch);
                ////////
                printTableLog(table, totalRows, time1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        printTotalUseTime(startTime);

    }

    private static void printTotalUseTime(long startTime) {
        System.out.println("\ntotal use time : " + ((System.currentTimeMillis() - startTime) / 1000) + "秒");
    }

    private static void printTableLog(String table, int totalRows, long time1) throws IOException {
        FileUtils.writeStringToFile(new File(FILE_PATH), table + "\r\n", StandardCharsets.UTF_8, true);
        System.out.println(BANK_STR + "use time : " + ((System.currentTimeMillis() - time1)) + "毫秒");
    }

    private static int getTableCount(Connection connection, String table) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select count(1) from " + table);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int count = rs.getInt(1);
            JdbcUtils.close(rs, ps, null);
            return count;
        }
        JdbcUtils.close(rs, ps, null);
        return 0;
    }

    private static String getInsertSQL(String table, List<String> fields) {
        //String sql = "insert into " + table + "(`id`,`name`) values (?, ?);";
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(table).append("(");
        for (int i = 0; i < fields.size(); i++) {
            sb.append("`").append(fields.get(i)).append("`").append((i != fields.size() - 1 ? ", " : ""));
        }
        sb.append(") ");
        sb.append("values (");
        for (int i = 0; i < fields.size(); i++) {
            sb.append("?").append((i != fields.size() - 1 ? ", " : ""));
        }
        sb.append(");");
        //System.out.println(sb.toString());
        return sb.toString();
    }

    private static List<String> getFields(Connection connection, String table) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from " + table + " limit 1");
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData data = rs.getMetaData();
        List<String> fields = new ArrayList<>(data.getColumnCount());
        while (rs.next()) {
            for (int i = 1; i <= data.getColumnCount(); i++) {
                //System.out.println("获得列" + i + "的字段名称:" + data.getColumnName(i));
                //System.out.println("获得列" + i + "对应数据类型的类:"+ data.getColumnClassName(i));
                fields.add(data.getColumnName(i));
            }
        }
        JdbcUtils.close(rs, ps, null);
        return fields;
    }

    public static List<String> getNeedImportTables() throws IOException {
        List<String> successTables = Collections.EMPTY_LIST;
        if (new File(FILE_PATH).exists()) {
            successTables = FileUtils.readLines(new File(FILE_PATH), StandardCharsets.UTF_8);
            if (successTables.size() > 0) {
                System.out.println("you will skip tables : " + successTables);
            }
        }
        List<String> successTablesFinal = successTables;

        List<String> totalTables = JdbcUtils.getTables(mainUrl, mainUsername, mainPw);

        // 需要导入数据的表  总表 - 排除的表[] - 已经导成功的表
        List<String> needImportTables = totalTables.stream().filter(item -> {
            boolean ok = true;
            for (String exPrefix : excludeTablePrefix) {
                if (item.startsWith(exPrefix)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                for (String exTable : excludeTables) {
                    if (item.equals(exTable)) {
                        ok = false;
                        break;
                    }
                }
            }
            if (ok) {
                for (String successTable : successTablesFinal) {
                    if (item.equals(successTable)) {
                        ok = false;
                        break;
                    }
                }
            }
            return ok;
        }).collect(Collectors.toList());
        return needImportTables;
    }
}
