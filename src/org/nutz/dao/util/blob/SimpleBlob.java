package org.nutz.dao.util.blob;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

/**
 * 如果数据已经是byte[],请使用javax.sql.rowset.serial.SerialBlob
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class SimpleBlob implements Blob, Serializable {

    private static final long serialVersionUID = 4192412466410263969L;
    
    protected File file;

    public SimpleBlob() {}
    
    /**
     * 如果数据已经是byte[],请使用javax.sql.rowset.serial.SerialBlob
     */
    public SimpleBlob(File f) {
        this.file = f;
    }

    public long length() throws SQLException {
        return file.length();
    }

    public byte[] getBytes(long pos, int length) throws SQLException {
        if (pos == 1 && length == length())
            try {
                return Streams.readBytes(getBinaryStream());
            } catch (IOException e) {
            }
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

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        Streams.writeAndClose(out, new FileInputStream(file));
    }
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{
        file = Jdbcs.getFilePool().createFile(".blob");
        Files.write(file, in);
    }
    
    public void setFile(File file) {
        this.file = file;
    }
}
