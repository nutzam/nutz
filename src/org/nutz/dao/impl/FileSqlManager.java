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
    
    protected Map<String, String> sqls = Collections.synchronizedMap(new LinkedHashMap<String, String>());

    protected String[] paths;
    
    protected boolean allowDuplicate = true;
    
    protected String pairBegin = "/*";
    protected String pairEnd = "*/";
    
    protected String regex = ".(sql|sqlx|sqls)$";
    
    protected boolean inited;
    
    public FileSqlManager() {
        paths = new String[]{};
    }

    public FileSqlManager(String... paths) {
        this.paths = paths;
    }

    public void refresh() {
        for (String path : paths) {
            List<NutResource> list = Scans.me().scan(path, regex);
            for (NutResource res : list) {
                int c = _count();
                log.debugf("load >> %s from root=%s", res.getName(), path);
                try {
                    add(res.getReader());
                }
                catch (IOException e) {
                    log.warnf("fail to load %s from root=%s", res.getName(), path, e);
                }
                log.debugf("load %d sql >> %s from root=%s", (_count() - c), res.getName(), path);
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
        _check_inited();
        String sql = sqls.get(key);
        if (sql == null)
            throw new SqlNotFoundException(key);
        return sql;
    }

    public Sql create(String key) throws SqlNotFoundException {
        _check_inited();
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
        _check_inited();
        return sqls.size();
    }
    
    public int _count() {
        return sqls.size();
    }

    public String[] keys() {
        _check_inited();
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
        _check_inited();
        sqls.remove(key);
    }
    
    public void setAllowDuplicate(boolean allowDuplicate) {
        this.allowDuplicate = allowDuplicate;
    }
    
    public boolean isAllowDuplicate() {
        return allowDuplicate;
    }
    
    public void setPaths(String[] paths) {
        this.paths = paths;
    }
    
    public String getRegex() {
        return regex;
    }

    public FileSqlManager setRegex(String regex) {
        this.regex = regex;
        return this;
    }
    
    public void setPairBegin(String pairBegin) {
        this.pairBegin = pairBegin;
    }
    
    public void setPairEnd(String pairEnd) {
        this.pairEnd = pairEnd;
    }
    
    public String getPairBegin() {
        return pairBegin;
    }
    
    public String getPairEnd() {
        return pairEnd;
    }
    
    public String[] getPaths() {
        return paths;
    }
    
    protected void _check_inited() {
        if (!inited) {
            synchronized (this) {
                if (!inited) {
                    refresh();
                    inited = true;
                }
            }
        }
    }
}
