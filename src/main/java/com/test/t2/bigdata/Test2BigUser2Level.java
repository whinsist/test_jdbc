package com.test.t2.bigdata;

import com.kugua.util.JdbcUtils;

import org.apache.commons.lang3.RandomUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * 查询等级大于5的用户 用户数据共120w
 *
 * select a.* from t_big_user a, t_level b where a.level_id=b.id and b.level>=5 order by a.id desc limit 10;
 *
 *
 * select a.* from t_big_user a LEFT JOIN t_level b on a.level_id=b.id where b.level>=5 order by a.id desc limit 10;
 *
 * #此方法效率最高 select a.* from t_big_user a where EXISTS (select 1 from t_level b where a.level_id=b.id and b.level>=5)
 * order by a.id desc limit 10;
 */
public class Test2BigUser2Level {

    public static void main(String[] args) throws SQLException {

        inserBigUser();

//        insertUserLevel();

    }

    public static void insertUserLevel() {
        Connection conn = JdbcUtils.getConnection();
        try {
            Long beginTime = System.currentTimeMillis();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("insert into t_level(name,level) values (?,?)");
            for (int i = 1; i <= 10; i++) {
                ps.setString(1, "等级" + i);
                ps.setInt(2, i);
                ps.execute();
            }
            conn.commit();
            Long endTime = System.currentTimeMillis();
            System.out.println("插入用户等级用时:" + (endTime - beginTime) / 1000 + "秒");
            JdbcUtils.free(null, ps, conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 使用PreparedStatement + 批处理
    public static void inserBigUser() {
        int total = 1200000;
        int everyCommitCount = 3000;
        Connection connBatch = JdbcUtils.getConnection();
        try {
            connBatch.setAutoCommit(false);
            Long beginTime = System.currentTimeMillis();
            PreparedStatement psBatch = connBatch.prepareStatement("insert into t_big_user(name,age,level_id) values (?,?,?)");
            for (int batchCounter = 1; batchCounter <= total; batchCounter++) {
                psBatch.setString(1, "name" + batchCounter);
                psBatch.setInt(2, RandomUtils.nextInt(0, 100));
                psBatch.setInt(3, RandomUtils.nextInt(11, 20));
                psBatch.addBatch();
                // 可以设置不同的大小；如50，100，500，1000等等
                if (batchCounter % everyCommitCount == 0) {
                    psBatch.executeBatch();
                    connBatch.commit();
                    psBatch.clearBatch();
                    System.out.println(batchCounter);
                }
            }

            // 提交剩余的数据
            psBatch.executeBatch();
            connBatch.commit();

            Long endTime = System.currentTimeMillis();
            System.out.println("pst+batch：" + (endTime - beginTime) / 1000 + "秒");

            JdbcUtils.free(null, psBatch, connBatch);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
