package org.nutz.conf;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.el.opt.custom.CustomMake;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutType;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mapl.Mapl;
import org.nutz.repo.org.objectweb.asm.Opcodes;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

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

    // zozoh 单利的话，没必要用这个吧 ...
    // private static final Lock lock = new ReentrantLock();

    private volatile static NutConf conf;

    private static NutConf me() {
        if (null == conf) {
            synchronized (NutConf.class) {
                if (null == conf)
                    conf = new NutConf();
            }
        }
        return conf;
    }

    private NutConf() {
        // 加载框架自己的一些配置
        loadResource(DEFAULT_CONFIG);
    }

    public static void load(String... paths) {
        me().loadResource(paths);
        CustomMake.me().init();
    }

    /**
     * 加载资源
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void loadResource(String... paths) {
        for (String path : paths) {
            List<NutResource> resources = Scans.me().scan(path, "\\.(js|json)$");

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
    public static void clear() {
        conf = null;
    }
    
    /**
     * 是否启用FastClass机制,会提高反射的性能,如果需要热部署,应关闭. 性能影响低于10%
     */
    public static boolean USE_FASTCLASS = !Lang.isAndroid && Lang.JdkTool.getMajorVersion() <= 8;
    /**
     * 是否缓存Mirror,配合FastClass机制使用,会提高反射的性能,如果需要热部署,应关闭.  性能影响低于10%
     */
    public static boolean USE_MIRROR_CACHE = true;
    /**
     * Map.map2object时的EL支持,很少会用到,所以默认关闭. 若启用, Json.fromJson会有30%左右的性能损失
     */
    public static boolean USE_EL_IN_OBJECT_CONVERT = false;
    /**
     * 调试Scans类的开关.鉴于Scans已经非常靠谱,这个开关基本上没用处了
     */
    public static boolean RESOURCE_SCAN_TRACE = false;
    /**
     * 是否允许非法的Json转义符,属于兼容性配置
     */
    public static boolean JSON_ALLOW_ILLEGAL_ESCAPE = true;
    /**
     * 若允许非法的Json转义符,是否把转义符附加进目标字符串
     */
    public static boolean JSON_APPEND_ILLEGAL_ESCAPE = false;
    /**
     * Aop类是否每个Ioc容器都唯一,设置这个开关是因为wendal还不确定会有什么影响,暂时关闭状态.
     */
    public static boolean AOP_USE_CLASS_ID = false;

    public static int AOP_CLASS_LEVEL = Opcodes.V1_6;
}
