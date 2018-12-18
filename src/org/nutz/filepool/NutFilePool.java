package org.nutz.filepool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.Regex;
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

        cursor = foundMax(home, home, 0);
        if (cursor < 0)
            cursor = 0;

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
        if (size > 0 && cursor >= size-1)
            cursor = -1;
        long id = ++cursor;
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
    
    /**
     * 公共FilePool的缓存池
     */
    protected static Map<String, FilePool> pools = new HashMap<String, FilePool>();
    /**
     * 获取指定路径下的FilePool,如果没有就新建一个
     * @param path 临时文件夹
     * @param limit 最大文件数量
     * @return 已有或新建的FilePool同步实例
     */
    public static FilePool getOrCreatePool(String path, long limit) {
        FilePool pool = pools.get(path);
        if (pool == null) {
            pool = new NutFilePool(path, limit);
            pool = new SynchronizedFilePool(pool);
            pools.put(path, pool);
        }
        return pool;
    }
    
    public static void clearPools() {
        pools.clear();
    }
    
    protected static long foundMax(File home, File current, int level) {
        // 最后一层了
        if (level == 8) {
            if (current.isDirectory())
                return -1;
            //System.out.println("found File!! "+current);
            return Pools.getFileId(home, current);
        }
        if (!current.isDirectory())
            return -1;
        int next_level = level+1;
        List<String> names = new ArrayList<String>();
        
        for (File f : current.listFiles()) {
            if (Regex.match("^([\\d|A-F]{2})([.][a-zA-Z]{1,})?$", f.getName())) {
                names.add(f.getName());
            }
        }
        Collections.sort(names);
        Collections.reverse(names);
        for (String name : names) {
            File next = new File(current, name);
            //System.out.println(next + ", level=" + next_level);
            long max = foundMax(home, next, next_level);
            if (max > -1) {
                return max;
            }
        }
        return -1;
    }
}
