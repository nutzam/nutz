package org.nutz.dao.impl.jdbc;

import java.io.File;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.nutz.dao.util.blob.SimpleBlob;
import org.nutz.filepool.FilePool;
import org.nutz.lang.Files;

public class BlobValueAdaptor extends AbstractFileValueAdaptor {

    public BlobValueAdaptor(FilePool pool) {
        super(pool);
        suffix = ".blob";
    }

    public Object get(ResultSet rs, String colName) throws SQLException {
        File f = this.createTempFile();
        Blob blob = rs.getBlob(colName);
        if (blob == null)
            return null;
        Files.write(f, blob.getBinaryStream());
        return new SimpleBlob(f);
    }

    public void set(PreparedStatement stat, Object obj, int i) throws SQLException {
        if (null == obj) {
            stat.setNull(i, Types.BLOB);
        } else {
            Blob blob = (Blob) obj;
            stat.setBlob(i, blob);
        }
    }

}
