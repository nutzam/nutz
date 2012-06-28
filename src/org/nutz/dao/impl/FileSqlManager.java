package org.nutz.dao.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

public class FileSqlManager extends AbstractSqlManager {
    
    private static final Log log = Logs.get();

    private String[] paths;

    private String regex;

    public FileSqlManager(String... paths) {
        this.paths = paths;
    }

    public String getRegex() {
        return regex;
    }

    public FileSqlManager setRegex(String regex) {
        this.regex = regex;
        return this;
    }

    public void refresh() {
        List<NutResource> list = Scans.me().loadResource(regex, paths);
        _sql_map = new HashMap<String, String>();
        for (NutResource ins : list) {
            if (log.isDebugEnabled())
                log.debug("Loading sqls from " + ins);
            try {
                loadSQL(ins.getReader());
            }
            catch (IOException e) {
                throw Lang.wrapThrow(e);
            }
        }
    }

}
