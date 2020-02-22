package com.test.t8.atomikos;

import com.mysql.jdbc.jdbc2.optional.MysqlXAConnection;
import com.mysql.jdbc.jdbc2.optional.MysqlXid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * @author Hong.Wu
 * @date: 22:03 2020/02/22
 */
public class XATest {

    public static void main(String[] args) throws Exception {
        Connection conn1 = DriverManager.getConnection("111", "root", "123456");
        XAConnection xaConn1 = new MysqlXAConnection((com.mysql.jdbc.Connection) conn1, true);
        XAResource rm1 = xaConn1.getXAResource();

        Connection conn2 = DriverManager.getConnection("111", "root", "123456");
        XAConnection xaConn2 = new MysqlXAConnection((com.mysql.jdbc.Connection) conn2, true);
        XAResource rm2 = xaConn2.getXAResource();

        byte[] gtrid = "g12345".getBytes();
        int fromatId = 1;
        try {
            byte[] bqual1 = "b0001".getBytes();
            Xid xid1 = new MysqlXid(gtrid, bqual1, fromatId);

            rm1.start(xid1, XAResource.TMNOFLAGS);
            PreparedStatement ps1 = conn1.prepareStatement(
                    "INSERT INTO article(author,content,title) VALUES (?, ?, ?); ",
                    Statement.RETURN_GENERATED_KEYS);
            ps1.executeUpdate();
            rm1.end(xid1, XAResource.TMSUCCESS);

            byte[] bqual2 = "b0002".getBytes();
            Xid xid2 = new MysqlXid(gtrid, bqual2, fromatId);
            rm2.start(xid2, XAResource.TMNOFLAGS);
            PreparedStatement ps2 = conn2.prepareStatement(
                    "INSERT INTO article(author,content,title) VALUES (?, ?, ?); ",
                    Statement.RETURN_GENERATED_KEYS);
            ps2.executeUpdate();
            rm2.end(xid2, XAResource.TMSUCCESS);

            int rm1_prepar = rm1.prepare(xid1);
            int rm2_prepar = rm2.prepare(xid2);

            boolean onePhase = false;
            if (rm1_prepar == XAResource.XA_OK && rm2_prepar == XAResource.XA_OK) {
                rm1.commit(xid1, onePhase);
                rm2.commit(xid2, onePhase);
            } else {
                rm1.rollback(xid1);
                rm2.rollback(xid2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
