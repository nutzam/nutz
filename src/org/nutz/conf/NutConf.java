package org.nutz.conf;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.nutz.el.opt.custom.CustomMake;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.util.NutType;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mapl.Mapl;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;
import org.nutz.resource.impl.FileResource;

/**
 * 配置加载器<br/>
 * 一个通用的配置加载器, 全局的加载配置文件, 这样, 在所有地方都可以使用这些配置信息了. 规则:<br/>
 * <ul>
 * <li>配置文件使用JSON格式.
 * <li>JSON第一层为配置项键值对, KEY 为配置项名称, 值为配置信息.
 * <li>使用文件数组, 或者文件目录的形式, 可以加载多个配置文件
 * <li>可以使用 include 关键字来引用其它配置文件, 值以数组形式.
 * <li>多配置文件的情况下后加载的配置会覆盖之前加载的配置,include引用的配置会覆盖引用前的配置.
 * <li>与JSON 相同, 配置项的值你可以转换成任意你想要的类型. 包括泛型, 可以使用 {@link NutType}
 * </ul>
 * 
 * @author juqkai(juqkai@gmail.com)
 * 
 */
public class NutConf {

    private static final Log log = Logs.get();

    private static final String DEFAULT_CONFIG = "org/nutz/conf/NutzDefaultConfig.js";

    // 所有的配置信息
    private Map<String, Object> map = new HashMap<String, Object>();
    private static final Lock lock = new ReentrantLock();

    private static NutConf conf;

    private static NutConf me() {
        lock.lock();
        try{
            if (null == conf) {
                if (null == conf)
                    conf = new NutConf();
            }
        } finally{
            lock.unlock();
        }
        return conf;
    }

    private NutConf() {
        // 加载框架自己的一些配置
        loadResource(DEFAULT_CONFIG);
    }

    public static void load(String... paths) {
        me().loadResource(paths);
        CustomMake.init();
    }

    /**
     * 加载资源
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void loadResource(String... paths) {
        for (String path : paths) {
            List<NutResource> resources;
            if (path.endsWith(".js")) {
                File f = Files.findFile(path);
                resources = new ArrayList<NutResource>();
                resources.add(new FileResource(f));
            } else {
                resources = Scans.me().scan(path, "\\.js$");
            }

            for (NutResource nr : resources) {
                try {
                    Object obj = Json.fromJson(nr.getReader());
                    if (obj instanceof Map) {
                        Map m = (Map) obj;
                        map = (Map) Mapl.merge(map, m);
                        for (Object key : m.keySet()) {
                            if (key.equals("include")) {
                                map.remove("include");
                                List<String> include = (List) m.get("include");
                                loadResource(include.toArray(new String[include.size()]));
                            }
                        }
                    }
                }
                catch (Throwable e) {
                    if (log.isWarnEnabled())
                        log.warn("Fail to load config?! for " + nr.getName(), e);
                }
            }
        }
    }

    /**
     * 读取一个配置项, 并转换成相应的类型.
     */
    public static Object get(String key, Type type) {
        return me().getItem(key, type);
    }

    /**
     * 读取配置项, 返回Map, List或者 Object. 具体返回什么, 请参考 JSON 规则
     */
    public static Object get(String key) {
        return me().getItem(key, null);
    }

    /**
     * 读取一个配置项, 并转换成相应的类型.
     * 
     * @param key
     * @param type
     * @return
     */
    private Object getItem(String key, Type type) {
        if (null == map) {
            return null;
        }
        if (null == type) {
            return map.get(key);
        }
        return Mapl.maplistToObj(map.get(key), type);
    }
    
    /**
     * 清理所有配置信息
     */
    public static void clear(){
        conf = null;
    }
}
