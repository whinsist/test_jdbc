package com.test.t3.difftable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kugua.util.JdbcAdd;
import com.kugua.util.JdbcUtils;

import org.apache.commons.io.FileUtils;


public class CompareTable {

    private Connection mainConn;
    private Connection otherConn;
    private String filePath;


    public CompareTable(String mainUrl, String mainUsername, String mainPw, String otherUrl, String otherUsername,
                        String otherPw, String filePath) {
        this.mainConn = JdbcUtils.getConnection(mainUrl, mainUsername, mainPw);
        this.otherConn = JdbcUtils.getConnection(otherUrl, otherUsername, otherPw);
        this.filePath = filePath;
    }


    public void match(List<String> tableList) {

        writeFile("###");

        try {
            for (String table : tableList) {
                doMatch(table, table);
            }
        } finally {
            JdbcUtils.close(mainConn);
            JdbcUtils.close(otherConn);
        }
    }

    /**
     * @param mainTableName 主表
     * @param otherTableName 副表
     *
     * @throws Exception
     */
    private void doMatch(String mainTableName, String otherTableName) {
        System.out.println("***********比较表名" + mainTableName + " " + otherTableName + "开始***********");
        StringBuffer tableDiffSql = new StringBuffer();
        PreparedStatement mainPs = null;
        PreparedStatement otherPs = null;
        ResultSet mainRs = null;
        ResultSet otherRs = null;
        Map<String, TableStructure> mainMap = new HashMap<String, TableStructure>();
        Map<String, TableStructure> otherMap = new HashMap<String, TableStructure>();
        try {
            String mainSql = "select * from " + mainTableName + " limit 1";
            String otherSql = "select * from " + otherTableName + " limit 1";
            // 获得主表结构
            mainPs = mainConn.prepareStatement(mainSql);
            mainRs = mainPs.executeQuery();
            ResultSetMetaData mainRsmd = mainRs.getMetaData();
            // 获得主表结构存入mainMap表中
            for (int i = 1; i <= mainRsmd.getColumnCount(); i++) {
                // 获得对应列的列名  数据类型 长度 是否为空
                String colName = mainRsmd.getColumnName(i).toLowerCase();
                String colType = mainRsmd.getColumnTypeName(i);
                int colLength = mainRsmd.getPrecision(i);
                int isNullable = mainRsmd.isNullable(i);
                TableStructure tableStructure = new TableStructure();
                tableStructure.setColName(colName);
                tableStructure.setColType(colType);
                tableStructure.setColLength(colLength);
                tableStructure.setIsNullable(isNullable);
                mainMap.put(colName, tableStructure);
            }

            // 查询表是否存在
            boolean hasTable = JdbcAdd.hasTable(otherConn, otherTableName);
            if (!hasTable) {
                System.out.println("表不存在------" + otherTableName);
                int count = JdbcAdd.getTableCount(mainConn, otherTableName);

                writeFile(tableDiffSql.append("#" + otherTableName + "表不存在 count=" + count + " TODO  ").toString());
                //String string = getCreateSQL(otherTableName, mainMap.get(mainTableName));
                throw new Exception(otherTableName + "表不存在 TODO");
            }

            // 获取副表的表结构
            otherPs = otherConn.prepareStatement(otherSql);
            otherRs = otherPs.executeQuery();
            ResultSetMetaData otherRsmd = otherRs.getMetaData();
            // 获得副表结构存入otherMap表中
            for (int i = 1; i <= otherRsmd.getColumnCount(); i++) {
                String colName = otherRsmd.getColumnName(i).toLowerCase();
                String colType = otherRsmd.getColumnTypeName(i);
                int colLength = otherRsmd.getPrecision(i);
                int isNullable = otherRsmd.isNullable(i);
                TableStructure tableStructure = new TableStructure();
                tableStructure.setColName(colName);
                tableStructure.setColType(colType);
                tableStructure.setColLength(colLength);
                tableStructure.setIsNullable(isNullable);
                otherMap.put(colName, tableStructure);
            }

            // 以主表结构为依据比较副表结构
            for (Entry<String, TableStructure> entry : mainMap.entrySet()) {
                String mainTableColName = entry.getKey();
                TableStructure mainTableStructure = entry.getValue();
                TableStructure otherTableStructure = otherMap.get(mainTableColName);
                if (otherTableStructure == null) {
                    // 副表没有该字段
                    // ALTER TABLE 【表名字】 ADD 【列名称】 INT(22) NOT NULL
                    String str = "ALTER TABLE " + mainTableName + " ADD COLUMN " + mainTableColName + " "
                            + mainTableStructure.getColType() +
                            ("DATETIME".equals(mainTableStructure.getColType()) || "DOUBLE".equals(
                                    mainTableStructure.getColType()) ? ""
                                    : "(" + mainTableStructure.getColLength() + ")")
                            +
                            convertNull(mainTableStructure.getIsNullable());
                    tableDiffSql.append(str + distend());
                    continue;
                }
                if (mainTableStructure.getColLength() != otherTableStructure.getColLength()) {
                    // 长度不一样 ALTER TABLE tb_ MODIFY COLUMN NAME VARCHAR(50);
                    String str = "ALTER TABLE " + mainTableName + " MODIFY COLUMN " + mainTableColName + " "
                            + mainTableStructure.getColType() + "(" + mainTableStructure.getColLength() + ")";
                    tableDiffSql.append(str + distend());
                }
                if (!mainTableStructure.getColType().equals(otherTableStructure.getColType())) {
                    // 类型不一样 ALTER TABLE tb_ MODIFY COLUMN NAME CHAR(50);
                    String str = "ALTER TABLE " + mainTableName + " MODIFY COLUMN " + mainTableColName + " "
                            + mainTableStructure.getColType() + "(" + mainTableStructure.getColLength() + ")";
                    tableDiffSql.append(str + distend());
                }
                if (mainTableStructure.getIsNullable() != otherTableStructure.getIsNullable()) {
                    // 是否为空不一样 ALTER TABLE tb_ MODIFY  zk_env VARCHAR(16) NOT NULL;
                    String str = "ALTER TABLE " + mainTableName + " MODIFY COLUMN " + mainTableColName + " "
                            + mainTableStructure.getColType() + "(" + mainTableStructure.getColLength() + ")" +
                            convertNull(mainTableStructure.getIsNullable());
                    tableDiffSql.append(str + distend());
                }
            }
            System.out.println("表修改语句：\n" + tableDiffSql.toString());
            // 把该表的不同信息写入文件中
            if (!"".equals(tableDiffSql.toString())) {
                writeFile(tableDiffSql.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("***********比较表名" + mainTableName + " " + otherTableName + "结束***********");
            JdbcUtils.close(mainRs, mainPs, null);
            JdbcUtils.close(otherRs, otherPs, null);
        }

    }

    private String getCreateSQL(String otherTableName) {
        String str = "create table sys_m_biz_system("
                + "id bigint(16) not null auto_increment, "
                + "bd_name  varchar(32),"
                + "primary key (id))"
                + " ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8'";
        ;
        return null;
    }


    private void writeFile(String info) {
        PrintWriter pw = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            pw = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));
            pw.println(info);
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    private String distend() {
        return ";\n";
    }

    private String convertNull(int isNullable) {
        if (ResultSetMetaData.columnNoNulls == isNullable) {
            return " not null";
        } else if (ResultSetMetaData.columnNullable == isNullable) {
            return " null";
        } else {
            return " unknown";
        }

    }
}
