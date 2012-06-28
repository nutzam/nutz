package org.nutz.dao.util.blob;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public class SimpleBlob implements Blob {

    private File file;

    public SimpleBlob(File f) {
        this.file = f;
    }

    public long length() throws SQLException {
        return file.length();
    }

    public byte[] getBytes(long pos, int length) throws SQLException {
        throw Lang.noImplement();
    }

    public InputStream getBinaryStream() throws SQLException {
        return Streams.buff(Streams.fileIn(file));
    }

    public long position(byte[] pattern, long start) throws SQLException {
        throw Lang.noImplement();
    }

    public long position(Blob pattern, long start) throws SQLException {
        throw Lang.noImplement();
    }

    public int setBytes(long pos, byte[] bytes) throws SQLException {
        throw Lang.noImplement();
    }

    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        throw Lang.noImplement();
    }

    public OutputStream setBinaryStream(long pos) throws SQLException {
        throw Lang.noImplement();
    }

    public void truncate(long len) throws SQLException {
        Files.write(file, new Byte[]{});
    }

    public void free() throws SQLException {
        Files.deleteFile(file);
    }

    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        throw Lang.noImplement();
    }

}
