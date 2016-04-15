package org.nutz.ioc.loader.combo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.aop.interceptor.async.AsyncAopIocLoader;
import org.nutz.aop.interceptor.ioc.TransIocLoader;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.ioc.loader.properties.PropertiesIocLoader;
import org.nutz.ioc.loader.xml.XmlIocLoader;
import org.nutz.ioc.meta.IocObject;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 融化多种IocLoader
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class ComboIocLoader implements IocLoader {

    private static final Log log = Logs.get();

    private List<IocLoader> iocLoaders = new ArrayList<IocLoader>();

    /**
     * 这个构造方法需要一组特殊的参数
     * <p/>
     * 第一种,以*开头,后面接类名, 如 <code>*org.nutz.ioc.loader.json.JsonLoader</code>
     * <p/>
     * 支持类别名: js, json, xml, annotation, anno, trans, async, props, tx, quartz分别对应其加载类
     * <p/>
     * 第二种,为具体的参数
     * <p/>
     * 处理规律, 当遇到第一种参数(*),则认为接下来的一个或多个参数为这一个IocLoader的参数,直至遇到另外一个*开头的参数
     * <p/>
     * <p/>
     * 例子:
     * <p/>
     * <code>{"*js","ioc/dao.js","ioc/service.js","*xml","ioc/config.xml", "*anoo", "net.wendal.nutzbook"}</code>
     * <p/>
     * 这样的参数, 会生成一个以{"ioc/dao.js","ioc/service.js"}作为参数的JsonLoader,一个以{"ioc/dao.xml"}
     * 作为参数的XmlIocLoader, 一个以"net.wendal.nutzbook"为参数的AnnotationIocLoader
     * 
     * @throws ClassNotFoundException
     *             如果*开头的参数所指代的类不存在
     */
    @SuppressWarnings("unchecked")
    public ComboIocLoader(String... args) throws ClassNotFoundException {
        if (loaders.isEmpty()) {
            loaders.put("js", JsonLoader.class);
            loaders.put("json", JsonLoader.class);
            loaders.put("xml", XmlIocLoader.class);
            loaders.put("annotation", AnnotationIocLoader.class);
            loaders.put("anno", AnnotationIocLoader.class);
            loaders.put("trans", TransIocLoader.class);
            loaders.put("tx", TransIocLoader.class);
            loaders.put("props", PropertiesIocLoader.class);
            loaders.put("properties", PropertiesIocLoader.class);
            loaders.put("async", AsyncAopIocLoader.class);
            try {
                loaders.put("cache",
                            (Class<? extends IocLoader>) Lang.loadClass("org.nutz.jcache.NutCacheIocLoader"));
            }
            catch (ClassNotFoundException e) {}
            try {
                loaders.put("quartz",
                            (Class<? extends IocLoader>) Lang.loadClass("org.nutz.integration.quartz.QuartzIocLoader"));
            }
            catch (ClassNotFoundException e) {}
        }
        ArrayList<String> argsList = null;
        String currentClassName = null;
        for (String str : args) {
            if (str.length() > 0 && str.charAt(0) == '*') {
                if (argsList != null)
                    createIocLoader(currentClassName, argsList);
                currentClassName = str.substring(1);
                argsList = new ArrayList<String>();
            } else {
                if (argsList == null) {
                    throw new IllegalArgumentException("ioc args without Loader ClassName. "
                                                       + Arrays.toString(args));
                }
                argsList.add(str);
            }
        }
        if (currentClassName != null)
            createIocLoader(currentClassName, argsList);

        Set<String> beanNames = new HashSet<String>();
        for (IocLoader loader : iocLoaders) {
            for (String beanName : loader.getName()) {
                if (!beanNames.add(beanName) && log.isWarnEnabled())
                    log.warnf("Found Duplicate beanName=%s, pls check you config! loader=%s", beanName,loader.getClass());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void createIocLoader(String className, List<String> args) throws ClassNotFoundException {
        Class<? extends IocLoader> klass = loaders.get(className);
        if (klass == null)
            klass = (Class<? extends IocLoader>) Lang.loadClass(className);
        iocLoaders.add((IocLoader) Mirror.me(klass).born(args.toArray(new Object[args.size()])));
    }

    public ComboIocLoader(IocLoader... loaders) {
        for (IocLoader iocLoader : loaders)
            if (iocLoader != null)
                iocLoaders.add(iocLoader);
    }

    public String[] getName() {
        ArrayList<String> list = new ArrayList<String>();
        for (IocLoader iocLoader : iocLoaders) {
            for (String name : iocLoader.getName())
                list.add(name);
        }
        return list.toArray(new String[list.size()]);
    }

    public boolean has(String name) {
        for (IocLoader iocLoader : iocLoaders)
            if (iocLoader.has(name))
                return true;
        return false;
    }

    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {

        for (IocLoader iocLoader : iocLoaders)
            if (iocLoader.has(name)) {
                IocObject iocObject = iocLoader.load(loading, name);
                if (log.isDebugEnabled())
                    log.debugf("Found IocObject(%s) in IocLoader(%s)",
                               name,
                               iocLoader.getClass().getSimpleName() + "@" + iocLoader.hashCode());
                return iocObject;
            }
        throw new ObjectLoadException("Object '" + name + "' without define!");
    }

    public void addLoader(IocLoader loader) {
        if (null != loader) {
            if (iocLoaders.contains(loader))
                return;
            iocLoaders.add(loader);
            if (log.isInfoEnabled())
                log.infof("add loader : %s : \n     - %s",
                          loader.getClass(),
                          Lang.concat("\n     - ", loader.getName()));
        }
    }

    /**
     * 类别名
     */
    protected static Map<String, Class<? extends IocLoader>> loaders = new HashMap<String, Class<? extends IocLoader>>();

    // TODO 这个方法好好整理一下 ...
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("/*ComboIocLoader*/\n{");
        for (IocLoader loader : iocLoaders) {
            String str = Json.toJson(loader);
            str = str.replaceFirst("[{]", ""); // 肯定有!!
            int index = str.lastIndexOf("}"); // 肯定有!!
            StringBuilder sb2 = new StringBuilder(str);
            sb2.setCharAt(index, ' ');
            sb.append(sb2).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
