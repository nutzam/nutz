package org.nutz.dao.impl.jdbc;

import java.io.File;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.util.blob.SimpleBlob;
import org.nutz.filepool.FilePool;
import org.nutz.lang.Files;

public class BlobValueAdaptor3 extends BlobValueAdaptor2 {

    public BlobValueAdaptor3(FilePool pool) {
        super(pool);
    }

    @Override
    public Object get(ResultSet rs, String colName) throws SQLException {
        InputStream ins = rs.getBinaryStream(colName);
        if (ins == null)
            return null;
        File f = this.createTempFile();
        Files.write(f, ins);
        return new SimpleBlob(f);
    }

}
