package org.nutz.dao.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.DaoException;
import org.nutz.dao.SqlManager;
import org.nutz.dao.SqlNotFoundException;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

/**
 * 基于行解析的SqlManager
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class FileSqlManager implements SqlManager {
    
    private static final Log log = Logs.get();
    
    Map<String, String> sqls = Collections.synchronizedMap(new LinkedHashMap<String, String>());

    protected String[] paths;
    
    protected boolean allowDuplicate = true;
    
    protected String pairBegin = "/*";
    protected String pairEnd = "*/";
    
    public FileSqlManager() {
    }

    public FileSqlManager(String... paths) {
        this.paths = paths;
        refresh();
    }

    public void refresh() {
        for (String path : paths) {
            List<NutResource> list = Scans.me().scan(path, ".(sql|sqlx|sqls)$");
            for (NutResource res : list) {
                int c = count();
                log.debugf("load >> %s from root=%s", res.getName(), path);
                try {
                    add(res.getReader());
                }
                catch (IOException e) {
                    log.warnf("fail to load %s from root=%s", res.getName(), path, e);
                }
                log.debugf("load %d sql >> %s from root=%s", (count() - c), res.getName(), path);
            }
        }
    }
    
    public void add(Reader r) throws IOException {
        try {
            BufferedReader br = null;
            if (r instanceof BufferedReader)
                br = (BufferedReader)r;
            else
                br = new BufferedReader(r);
            StringBuilder key = new StringBuilder();
            StringBuilder sb = new StringBuilder();
            OUT: while (br.ready()) {
                String line = Streams.nextLineTrim(br);
                if (line == null)
                    break;
                if (line.startsWith(pairBegin)) {
                    if (key.length() > 0 && line.contains(pairEnd) && !line.endsWith(pairEnd)) {
                        sb.append(line);
                        continue;
                    }
                    if (key.length() > 0 && sb.length() > 0) {
                        addSql(key.toString(), sb.toString());
                    }
                    key.setLength(0);
                    sb.setLength(0);
                    
                    if (line.endsWith(pairEnd)) {
                        if (line.length() > 4)
                            key.append(line.substring(2, line.length() - 2).trim());
                        continue;
                    } else {
                        key.append(line.substring(2).trim());
                        while (br.ready()) {
                            line = Streams.nextLineTrim(br);
                            if (line == null)
                                break OUT;
                            if (line.endsWith(pairEnd)) {
                                if (line.length() > 2)
                                    key.append(line.substring(0, line.length() - 2).trim());
                                continue OUT;
                            } else {
                                key.append(line);
                            }
                        }
                    }
                }
                if (key.length() == 0) {
                    log.infof("skip not key sql line %s", line);
                    continue;
                }
                if (sb.length() > 0)
                    sb.append("\n");
                sb.append(line);
            }
            
            // 最后一个sql也许是存在的
            if (key.length() > 0 && sb.length() > 0) {
                addSql(key.toString(), sb.toString());
            }
        }
        finally {
            Streams.safeClose(r);
        }
    }
    
    public String get(String key) throws SqlNotFoundException {
        String sql = sqls.get(key);
        if (sql == null)
            throw new SqlNotFoundException(key);
        return sql;
    }

    public Sql create(String key) throws SqlNotFoundException {
        return Sqls.create(get(key));
    }

    public List<Sql> createCombo(String... keys) {
        if (keys.length == 0)
            keys = keys();
        List<Sql> list = new ArrayList<Sql>(keys.length);
        for (String key : keys) {
            list.add(create(key));
        }
        return list;
    }

    public int count() {
        return sqls.size();
    }

    public String[] keys() {
        Set<String> keys = sqls.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    public synchronized void addSql(String key, String value) {
        log.debugf("key=[%s], sql=[%s]", key, value);
        if (!isAllowDuplicate() && sqls.containsKey(key))
            throw new DaoException("Duplicate sql key=[" +key + "]");
        sqls.put(key, value);
    }

    public void remove(String key) {
        sqls.remove(key);
    }
    
    public void setAllowDuplicate(boolean allowDuplicate) {
        this.allowDuplicate = allowDuplicate;
    }
    
    public boolean isAllowDuplicate() {
        return allowDuplicate;
    }
    
    /**
     * 废弃的方法,仅为兼容性保留空方法
     */
    @Deprecated
    public String getRegex() {
        return null;
    }

    @Deprecated
    public FileSqlManager setRegex(String regex) {
        log.warn("SqlManager regex is Deprecated!! it will be ignore!!");
        return this;
    }
}
