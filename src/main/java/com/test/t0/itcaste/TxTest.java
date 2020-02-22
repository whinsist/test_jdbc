package com.test.t0.itcaste;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.kugua.util.JdbcUtils;

/**
 * 2008-12-7
 *
 * @author <a href="mailto:liyongibm@gmail.com">liyong</a>
 *
 *         先解释一下：
 *
 *         事务指定一个隔离级别，该隔离级别定义一个事务必须与由其他事务进行的资源或数据更改相隔离的程度。隔离级别从允许的并发副作用（例如，脏读或虚拟读取）的角度进行描述。 a:脏读取：一个事务读取了另外一个并行事务未提交的数据
 *         b:不可重复读取：一个事务再次读取之前读过的数据时得到的数据不一致，被另外一个事务修改。
 *
 *         把甲的安全级别设成是是允许自己可重复读的。那么甲事务在执行中读了一次ID是10的数据的资金是1000， 在执行过程中第二次它再去读这个ID10的资金有可能就不是1000了，因为乙在甲执行第二次查询之前第一次查询之后将这条数据的资金修改并提交了。
 *
 *         不可重复读是指在一个事务内，多次读同一数据。在这个事务还没有结束时，另外一个事务也访问该同一数据。 那么，在第一个事务中的两次读数据之间，由于第二个事务的修改，那么第一个事务两次读到的数据可能是不一样的。这样就发生了在一个事务内两次读到的数据是不一样的，因此称为是不可重复读。
 *
 *
 *
 *         c:虚读：一个事务重新执行一个查询，返回的记录包含了其他事务提交的新记录 设定事务的隔离级别：con.setTransactionIsolation(Connection.isolationLevel);
 *
 *         四种隔离级别： con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);//最底级别：只保证不会读到非法数据，上述3个问题有可能发生
 *         con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //默认级别：可以防止脏读
 *         con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);//可以防止脏读和不可重复读取
 *         con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); //最高级别：防止上述3种情况，事务串行执行，慎用
 *
 *
 *         界面操作： https://www.cnblogs.com/huanongying/p/7021555.html
 */

/**
 * 数据库中的不可重复读问题
 想不明白不可重复读有什么坏处呢？比如读一个数据，第一次读为1000，然后中间有其他事物将它修改为2000，我再读是2000，这样的结果不是好的吗？为什么说不可重复读是不好的。望大神解答

 在不可重复读里进行重复读操作，不一定有问题，这个由业务决定，实际上很多业务场景重复读不一样的数据，（后一次是最新数据）是对的，反而重复读取的数据一样的话是错的。
 当设置事务隔离级别为不可重复读时，在事务代码里是可以重复读操作的，这个看上去很矛盾， 就像十字路口竖立了一个牌子，上面写着“不可闯红灯”，但是其实你硬要闯也是可以的，
 那个提示只是告诉你有可能出现危险！这里一样的道理，在“不可重复读”等级下进行了重复读其实也是可以的，mysql本身不会因为你重复读强制报错或终止，但是可能会因为重复读导致问题，
 也有可能没有问题，并不是说一定会出错，有些场景，两次读不一样，第二次获取到的是最新的数据，确实可能是好的，反而有些场景重复读数据保持一样（明明后边数据被别的事务更新了）可能出错。
 至于哪些场景这样会有问题，我觉得应该是那种生成瞬时数据的场景，比如今天12:00:00网站访问量是1000，有个事务正要拿这个时间点的1000处理，然后返回生成一个报表，这个时候，
 另一个事务把这个1000改成了2000，但这个2000是12:00:01的访问量，这种场景，重复读就有问题，因为不精确了。但有的场景，比如只要求生成最新的报表，不要求时间，越新越好，这里重复读又没问题，
 还有类似余额处理，如果是像楼上说的刷卡场景，那种update 两次读取不一样（后边是最新数据）我觉得才是对的，所以需要根据业务场景，有了正确的隔离等级还要配合适当的代码才能决定是对还是错 。
 */

public class TxTest {

    /**
     * @param args
     *
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {
        test();
    }

    static void test() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = JdbcUtils.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            st = conn.createStatement();
            String sql = "update tx_user set money=money-10 where id=1";
            st.executeUpdate(sql);

            int x = 12 / 0;

            sql = "update tx_user set money=money+10 where id=2";
            st.executeUpdate(sql);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            JdbcUtils.free(null, st, conn);
        }
    }
}
