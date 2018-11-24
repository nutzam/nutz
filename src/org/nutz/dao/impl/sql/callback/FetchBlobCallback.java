package org.nutz.dao.impl.sql.callback;

import org.nutz.dao.impl.jdbc.BlobValueAdaptor;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 这个回调将返回一个 Blob 值
 * @author wizzercn(wizzer.cn@gmail.com)
 */
public class FetchBlobCallback implements SqlCallback {

    public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
        if (null != rs && rs.next())
            return new BlobValueAdaptor(Jdbcs.getFilePool()).get(rs, 1);
        return null;
    }
}
