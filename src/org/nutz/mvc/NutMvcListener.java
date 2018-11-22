package org.nutz.mvc;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

/**
 * <p>
 * 定义全局Ioc容器,并实现IocProvider接口,以便与@IocBy注解的融合.
 * </p>
 * <p>
 * <b>本实现使用ComboIocLoader作为加载器,所以允许加载其他人员IocLoader定义</b>
 * </p>
 * <p>
 * 基本配置流程
 * </p>
 * <p>
 * 1. 在web.xml声明本监听器
 * </p>
 * <p>
 * 2. 在web.xml中声明一个param-name叫nutz-iocby,内容是传统ComboIocLoader的参数,用逗号分隔
 * </p>
 * <p>
 * 2. 或者在/WEB-INF下放一个nutz.properties, 内容是nutz-iocby=XXXXXXXX
 * </p>
 * <p>
 * 3. 在MainModule中声明@IocBy(type=NutMvcListener.class)
 * </p>
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class NutMvcListener implements ServletContextListener, IocProvider {

    public static String PROP_LOCATION = "nutz-properties-location";
    public static String IOCBY = "iocby";
    protected ServletContext sc;
    protected PropertiesProxy pp;

    private static final Log log = Logs.get();

    protected static Ioc ioc;

    /**
     * 返回全局Ioc对象,如果未经初始化, 这里就会抛出异常
     */
    public static Ioc ioc() {
        if (ioc == null)
            throw new IllegalArgumentException("NutMvcListener NOT init!!! check your web.xml!!");
        return ioc;
    }

    public void contextInitialized(ServletContextEvent event) {
        sc = event.getServletContext();
        Scans.me().init(sc);
        findConfig();
        initIoc();

        // TODO 其他可配置项.
    }

    /**
     * 首先,载入需要的配置信息, 分别从nutz.properties和ServletContext的上下文获取.
     * <p/>
     * 子类可以覆盖这个方法实现从任意方式加载配置
     */
    protected void findConfig() {
        String propLocation = sc.getInitParameter(PROP_LOCATION);
        if (Strings.isBlank(propLocation)) {
            propLocation = "nutz.properties";
        }
        PropertiesProxy pp = new PropertiesProxy();
        Enumeration<String> params = sc.getInitParameterNames();
        while (params.hasMoreElements()) {
            String name = (String) params.nextElement();
            if (name.startsWith("nutz-")) {
                pp.put(name, sc.getInitParameter(name).trim());
            }
        }
        // 先找找classpath
        InputStream in = getClass().getClassLoader().getResourceAsStream("/" + propLocation);
        if (in == null) {
            in = sc.getResourceAsStream("/WEB-INF/" + propLocation);
        }
        if (in == null) {
            log.debug(propLocation + " not found");
        } else {
            pp = new PropertiesProxy(in);
            Streams.safeClose(in);
        }
        this.pp = pp;
    }

    /**
     * 初始化Ioc容器,使用ComboIocLoader作为配置方式
     */
    protected void initIoc() {
        String key = "nutz-" + IOCBY;
        String iocby = pp.get(key);
        if (Strings.isBlank(iocby)) {
            throw new RuntimeException(key + " not found nutz.ini or context-param !!");
        }
        String[] args = Strings.splitIgnoreBlank(iocby);
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
        }
        log.info("init Ioc by args=" + Arrays.toString(args));
        try {
            ioc = new NutIoc(new ComboIocLoader(args));
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 容器销毁时,检查Ioc是否已经关闭,没有的话就关闭之.
     */
    public void contextDestroyed(ServletContextEvent event) {
        if (ioc() != null) {
            Ioc ioc = ioc();
            if (ioc instanceof NutIoc) {
                boolean deposed = (Boolean) Mirror.me(ioc).getValue(ioc, "deposed");
                if (!deposed)
                    ioc.depose();
            }
        }
    }

    /**
     * 这里与IocBy结合起来. 注意,这个实现会忽略IocBy的args参数.
     */
    public Ioc create(NutConfig config, String[] args) {
        if (args != null && args.length > 0) {
            if (log != null)
                log.warn("args ignore : " + Arrays.toString(args));
        }
        return ioc();
    }

}
