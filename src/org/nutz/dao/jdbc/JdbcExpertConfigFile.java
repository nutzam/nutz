package org.nutz.dao.jdbc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.nutz.dao.DaoException;
import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class JdbcExpertConfigFile {

    private Map<String, Class<? extends JdbcExpert>> experts;
    
    private Map<Pattern, Class<? extends JdbcExpert>> _experts; 

    private Map<String, Object> config;

    private NutFilePool pool;
    
    private static final Log log = Logs.get();

    JdbcExpertConfigFile init() {
        // 即使初始化失败,也继续执行
    	String home = config.get("pool-home").toString();
        try {
            home = Disks.normalize(home);
            if (home == null)
            	home = config.get("pool-home").toString();
            long max = ((Number) config.get("pool-max")).longValue();
            pool = new NutFilePool(home, max);
        } catch (Throwable e) {
            if (log.isWarnEnabled())
                log.warnf("NutDao FilePool create fail!! Blob and Clob Support is DISABLE!! Home="+home, e);
        }
        return this;
    }

    public JdbcExpert getExpert(String str) {
        Class<? extends JdbcExpert> type = experts.get(str);
        return Mirror.me(type).born(config);
    }

    public JdbcExpert matchExpert(String dbName) {
        for (Entry<Pattern, Class<? extends JdbcExpert>> entry : _experts.entrySet()) {
            if (entry.getKey().matcher(dbName).find())
                return Mirror.me(entry.getValue()).born(this);
        }
        return null;
    }

    /**
     * 注意,返回的Map实例不允许被修改
     */
    public Map<String, Class<? extends JdbcExpert>> getExperts() {
        return Collections.unmodifiableMap(experts);
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public NutFilePool getPool() {
        if (pool == null) {
            if (log.isWarnEnabled())
                log.warnf("NutDao FilePool create fail!! Blob and Clob Support is DISABLE!!");
            throw new DaoException("NutDao FilePool create fail!! Blob and Clob Support is DISABLE!!");
        }
        return pool;
    }

    // 在 fromJson 的时候，会被调用
    public void setExperts(Map<String, Class<? extends JdbcExpert>> experts) {
        this.experts = experts;
        this._experts = new HashMap<Pattern, Class<? extends JdbcExpert>>();
        for (Entry<String, Class<? extends JdbcExpert>> entry : experts.entrySet()) {
            //忽略大小写,并且让换行符与.能够匹配
            _experts.put(Pattern.compile(entry.getKey(), Pattern.DOTALL & Pattern.CASE_INSENSITIVE), entry.getValue());
        }
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

}
