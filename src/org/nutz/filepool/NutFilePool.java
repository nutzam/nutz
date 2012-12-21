package org.nutz.filepool;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class NutFilePool implements FilePool {

    private static final Log log = Logs.get();

    public NutFilePool(String homePath) {
        this(homePath, 0);
    }

    public NutFilePool(String homePath, long size) {
        if (log.isInfoEnabled()) {
            log.infof("Init file-pool by: %s [%s]", homePath, size);
        }

        this.size = size;
        this.home = Files.createDirIfNoExists(homePath);

        if (!home.isDirectory())
            throw Lang.makeThrow(    "Path error '%s'! ,You must declare a real directory as the '%s' home folder.",
                                    homePath,
                                    this.getClass().getName());

        home = new File(Disks.normalize(homePath));

        if (log.isDebugEnabled()) {
            log.debugf("file-pool.home: '%s'", home.getAbsolutePath());
        }

        File last = home;
        String[] subs = null;
        while (last.isDirectory()) {
            subs = last.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.matches("^([\\d|A-F]{2})([.][a-zA-Z]{1,})?$");
                }
            });
            if (null != subs && subs.length > 0) {
                last = new File(last.getAbsolutePath() + "/" + subs[subs.length - 1]);
                if (last.isFile()) {
                    cursor = Pools.getFileId(home, last);
                    break;
                }
            } else {
                break;
            }
        }

        if (log.isInfoEnabled())
            log.infof("file-pool.cursor: %s", cursor);
    }

    private File home;
    private long cursor;
    private long size;

    public void clear() {
        Files.deleteDir(home);
        Files.makeDir(home);
        cursor = 0;
    }

    public File createFile(String suffix) {
        if (size > 0 && cursor >= size)
            cursor = -1;
        long id = ++cursor;
        if (size > 0 && id >= size)
            Lang.makeThrow("Id (%d) is out of range (%d)", id, size);
        File re = Pools.getFileById(home, id, suffix);
        if (!re.exists())
            try {
                Files.createNewFile(re);
            }
            catch (IOException e) {
                throw Lang.wrapThrow(e);
            }
        return re;
    }

    public long current() {
        return cursor;
    }

    public long getFileId(File f) {
        try {
            return Pools.getFileId(home, f);
        }
        catch (Exception e) {
            return -1;
        }
    }

    public File removeFile(long fId, String suffix) {
        File f = Pools.getFileById(home, fId, suffix);
        Files.deleteFile(f);
        return f;
    }

    public boolean hasFile(long fId, String suffix) {
        File f = Pools.getFileById(home, fId, suffix);
        return f.exists();
    }

    public File getFile(long fId, String suffix) {
        File f = Pools.getFileById(home, fId, suffix);
        if (!f.exists())
            return null;
        return f;
    }

    public File returnFile(long fId, String suffix) {
        File f = Pools.getFileById(home, fId, suffix);
        if (!f.exists())
            try {
                Files.createNewFile(f);
            }
            catch (IOException e) {
                throw Lang.wrapThrow(e);
            }
        return f;
    }

    public File createDir() {
        if (size > 0 && cursor >= size)
            cursor = -1;
        long id = ++cursor;
        if (size > 0 && id >= size)
            Lang.makeThrow("Id (%d) is out of range (%d)", id, size);

        return Files.createDirIfNoExists(Pools.getFilePathById(home, id, null));
    }

    public File removeDir(long fId) {
        File f = Pools.getFileById(home, fId, null);
        if (f.isDirectory()) {
            Files.deleteDir(f);
        } else {
            Files.deleteFile(f);
        }
        return f;
    }

    public boolean hasDir(long fId) {
        File f = Pools.getFileById(home, fId, null);
        return f.exists();
    }

    public File getDir(long fId) {
        File f = Pools.getFileById(home, fId, null);
        if (!f.exists())
            return null;
        return f;
    }

    public File returnDir(long fId) {
        File f = Pools.getFileById(home, fId, null);
        if (!f.exists())
            Files.makeDir(f);
        return f;
    }

}
