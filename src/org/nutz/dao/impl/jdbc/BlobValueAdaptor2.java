package org.nutz.dao.impl.jdbc;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.nutz.filepool.FilePool;

public class BlobValueAdaptor2 extends BlobValueAdaptor {

    public BlobValueAdaptor2(FilePool pool) {
        super(pool);
    }

    public void set(PreparedStatement stat, Object obj, int i) throws SQLException {
        if (null == obj) {
            stat.setNull(i, Types.BLOB);
        } else {
            Blob blob = (Blob) obj;
            stat.setBinaryStream(i, blob.getBinaryStream(), blob.length());
        }
    }

}
