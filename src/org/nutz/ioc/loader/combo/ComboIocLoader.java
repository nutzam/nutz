package org.nutz.ioc.loader.combo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.ioc.loader.json.JsonLoader;
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
     * <p/>1.b.45版开始支持类别名: js , json, xml, annotation 分别对应其加载类
     * <p/>
     * 第二种,为具体的参数
     * <p/>
     * 处理规律, 当遇到第一种参数(*),则认为接下来的一个或多个参数为这一个IocLoader的参数,直至遇到另外一个*开头的参数
     * <p/>
     * <p/>
     * 例子:
     * <p/>
     * <code>{"*org.nutz.ioc.loader.json.JsonLoader","dao.js","service.js","*org.nutz.ioc.loader.xml.XmlIocLoader","config.xml"}</code>
     * <p/>
     * 这样的参数, 会生成一个以{"dao.js","service.js"}作为参数的JsonLoader,一个以{"dao.xml"}
     * 作为参数的XmlIocLoader
     * 
     * @throws ClassNotFoundException
     *             如果*开头的参数所指代的类不存在
     */
    public ComboIocLoader(String... args) throws ClassNotFoundException {
        ArrayList<String> argsList = null;
        String currentClassName = null;
        for (String str : args) {
            if (str.length() > 0 && str.charAt(0) == '*') {
                if (argsList != null)
                    createIocLoader(currentClassName, argsList);
                currentClassName = str.substring(1);
                argsList = new ArrayList<String>();
            } else
                argsList.add(str);
        }
        if (currentClassName != null)
            createIocLoader(currentClassName, argsList);
        
        Set<String> beanNames = new HashSet<String>();
        for (IocLoader loader : iocLoaders) {
            for (String beanName : loader.getName()) {
                if (!beanNames.add(beanName) && log.isWarnEnabled())
                    log.warnf("Found Duplicate beanName=%s, pls check you config!", beanName);
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
                    log.debugf("Found IocObject(%s) in IocLoader(%s)", name, iocLoader.getClass().getSimpleName() + "@" + iocLoader.hashCode());
                return iocObject;
            }
        throw new ObjectLoadException("Object '" + name + "' without define!");
    }
    
    /**
     * 类别名
     */
    private static Map<String, Class<? extends IocLoader>> loaders = new HashMap<String, Class<? extends IocLoader>>();
    static {
        loaders.put("js", JsonLoader.class);
        loaders.put("json", JsonLoader.class);
        loaders.put("xml", XmlIocLoader.class);
        loaders.put("annotation", AnnotationIocLoader.class);
    }

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
