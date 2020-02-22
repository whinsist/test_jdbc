package com.kugua.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Hong.Wu
 * @date: 15:10 2020/02/07
 */
public class JdbcAdd {
    public static boolean hasTable(Connection connection, String table) {
        ResultSet rs = null;
        boolean result = false;
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            rs = dbMetaData.getTables(null, null, table, null);
            result = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.close(rs, null, null);
        }
        return result;
    }

    public static Integer getTableCount(Connection connection, String tableName) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sqlCount = "select count(1) from " + tableName;
            ps = connection.prepareStatement(sqlCount);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.free(rs, ps, null);
        }
        return null;
    }
}
