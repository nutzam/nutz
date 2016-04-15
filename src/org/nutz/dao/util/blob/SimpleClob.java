package org.nutz.dao.util.blob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public class SimpleClob implements Clob, Serializable {

    private static final long serialVersionUID = -5465130240603178708L;
    
    private File file;
    
    public SimpleClob() {}

    public SimpleClob(File f) {
        this.file = f;
    }

    public long length() throws SQLException {
        return file.length();
    }

    public String getSubString(long pos, int length) throws SQLException {
        if (pos < 1)
            throw new SQLException("pos<1");
        pos--;
        String str = Files.read(file);
        if (pos >= length)
            throw new IllegalArgumentException("pos="+pos);
        if (pos + length >= length())
            return str.substring((int)pos);
        return str.substring((int)pos, (int)(pos + length - 1));
    }

    public Reader getCharacterStream() throws SQLException {
        return Streams.fileInr(file);
    }

    public InputStream getAsciiStream() throws SQLException {
        return Streams.buff(Streams.fileIn(file));
    }

    public long position(String searchstr, long start) throws SQLException {
        throw Lang.noImplement();
    }

    public long position(Clob searchstr, long start) throws SQLException {
        throw Lang.noImplement();
    }

    public int setString(long pos, String str) throws SQLException {
        throw Lang.noImplement();
    }

    public int setString(long pos, String str, int offset, int len) throws SQLException {
        throw Lang.noImplement();
    }

    public OutputStream setAsciiStream(long pos) throws SQLException {
        throw Lang.noImplement();
    }

    public Writer setCharacterStream(long pos) throws SQLException {
        throw Lang.noImplement();
    }

    public void truncate(long len) throws SQLException {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.setLength(len);
            raf.close();
        }
        catch (FileNotFoundException e) {
            throw Lang.wrapThrow(e);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    public void free() throws SQLException {
        Files.deleteFile(file);
    }

    public Reader getCharacterStream(long pos, long length) throws SQLException {
        throw Lang.noImplement();
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        Streams.writeAndClose(out, new FileInputStream(file));
    }
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{
        file = Jdbcs.getFilePool().createFile(".clob");
        Files.write(file, in);
    }
}
