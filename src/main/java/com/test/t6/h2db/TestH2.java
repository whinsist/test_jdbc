package com.test.t6.h2db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Hong.Wu
 * @date: 16:51 2019/12/03
 */
public class TestH2 {
    //一种是内存模式，这种模式不会落地持久化，所以比较适合测试，关闭连接后数据库就清空，只需要添加
//    private static final String JDBC_URL = "jdbc:h2:mem:DBName;DB_CLOSE_DELAY=-1";
    // 另外一种是持久化模式，这种模式会将数据落地持久化到指定的目录，生成与数据库同名的.mv.db文件
//    private static final String JDBC_URL = "jdbc:h2:./test";
    private static final String JDBC_URL = "jdbc:h2:~/test";
    // 服务式就是指定一个tcp的远程目录
//    private static final String JDBC_URL = "jdbc:h2:tcp://localhost/~/test”;

    //用户名
    private static final String USER = "sa";
    //连接数据库时使用的密码，默认为空
    private static final String PASSWORD = "";
    //连接H2数据库时使用的驱动类，org.h2.Driver这个类是由H2数据库自己提供的，在H2数据库的jar包中可以找到
    private static final String DRIVER_CLASS = "org.h2.Driver";


    /*drop table if exists t_test ;
    create table t_test
            (
                    id int primary key not null,
                    name varchar(20)
    )
    insert into t_test(id,name) values(1, 'aaa');
    insert into t_test(id,name) values(2, 'bbb');
    */

    public static void main(String[] args) throws Exception {
        // 加载H2数据库驱动
        Class.forName(DRIVER_CLASS);
        // 根据连接URL，用户名，密码获取数据库连接
        Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        System.out.println(connection);
        DatabaseMetaData dbMetaData = connection.getMetaData();
        ResultSet rs = dbMetaData.getTables(null, null, null, new String[] {"TABLE"});
        while (rs.next()) {
            System.out.println(rs.getString("TABLE_NAME") + "表类型：" + rs.getString("TABLE_TYPE") +rs.getString("TABLE_CAT")+rs.getString("TABLE_SCHEM")+rs.getString("REMARKS") );
        }

        PreparedStatement ps = connection.prepareStatement("SELECT * FROM T_TEST ");
        rs = ps.executeQuery();
        while(rs.next()) {
            System.out.println(rs.getObject(1));
        }


    }   
}
