package com.kugua.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * final该工具了不允许继承 构造方法私有化--> 不用产生实例 静态代码块--> 注册驱动  类装载到虚拟机只执行一次
 */
public final class JdbcUtils {

    private JdbcUtils() {
    }

    static {
        try {
            //指定连接类型  forName：根据类名  类 装载到虚拟机中去
            Class.forName(Config.DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() {
        return getConnection(Config.URL + "?useUnicode=true&characterEncoding=UTF-8", Config.USERNAME, Config.PASSWORD);
    }

    /**
     * 获取连接
     *
     * @param url
     * @param user
     * @param password
     */
    public static Connection getConnection(String url, String user, String password) {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        Connection connection = getConnection("jdbc:mysql://10.69.0.53:44450/rightcloud", "root", "1q2w3e4r");
        System.out.println(connection);
    }


    public static List<String> getTables(String url, String user, String password) {
        List<String> tables = new LinkedList<String>();
        Connection connection = getConnection(url, user, password);
        ResultSet rs = null;
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            rs = dbMetaData.getTables(null, null, null, new String[]{"TABLE"});
            while (rs.next()) {
                //System.out.println(rs.getString("TABLE_NAME") + "表类型：" + rs.getString("TABLE_TYPE") +rs.getString("TABLE_CAT")+rs.getString("TABLE_SCHEM")+rs.getString("REMARKS") );
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, null, connection);
        }
        return tables;
    }




    public static void close(ResultSet rs) {
        close(rs, null, null);
    }

    public static void close(Statement psts) {
        close(null, psts, null);
    }

    public static void close(Connection conn) {
        close(null, null, conn);
    }

    public static void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(ResultSet rs, Statement psts, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (psts != null) {
            try {
                psts.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadParams(PreparedStatement psts, Object... params) throws SQLException {
        psts.clearParameters();
        int idx = 1;
        for (Object param : params) {
            psts.setObject(idx, param);
            idx++;
        }
    }

    public static void free(ResultSet rs, Statement st, Connection conn) {
        close(rs, st, conn);
    }


}
