package org.nutz.ioc.aop.config.impl.simple;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean
public class OneObject {
    
    public static int COUNT;
    
    private static final Log log = Logs.get();

    public OneObject() {
        COUNT ++;
        log.info("COUNT="+COUNT);
    }
}
