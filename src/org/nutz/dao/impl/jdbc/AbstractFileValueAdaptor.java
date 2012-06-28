package org.nutz.dao.impl.jdbc;

import java.io.File;

import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.filepool.FilePool;

abstract class AbstractFileValueAdaptor implements ValueAdaptor {

    private FilePool pool;

    String suffix;

    AbstractFileValueAdaptor(FilePool pool) {
        this.pool = pool;
    }

    File createTempFile() {
        return pool.createFile(suffix);
    }

}
