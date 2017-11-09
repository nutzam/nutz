package org.nutz.mvc.impl;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.Segments;
import org.nutz.lang.util.ClassMeta;
import org.nutz.lang.util.ClassMetaReader;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.ModuleScanner;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.ObjectInfo;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Chain;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.PUT;
import org.nutz.mvc.annotation.PathMap;
import org.nutz.resource.Scans;

public abstract class Loadings {

    private static final Log log = Logs.get();

    public static ActionInfo createInfo(Class<?> type) {
        ActionInfo ai = new ActionInfo();
        evalEncoding(ai, Mirror.getAnnotationDeep(type, Encoding.class));
        evalHttpAdaptor(ai, Mirror.getAnnotationDeep(type, AdaptBy.class));
        evalActionFilters(ai, Mirror.getAnnotationDeep(type, Filters.class));
        evalPathMap(ai, Mirror.getAnnotationDeep(type, PathMap.class));
        evalOk(ai, Mirror.getAnnotationDeep(type, Ok.class));
        evalFail(ai, Mirror.getAnnotationDeep(type, Fail.class));
        evalAt(ai, Mirror.getAnnotationDeep(type, At.class), type.getSimpleName());
        evalActionChainMaker(ai, Mirror.getAnnotationDeep(type, Chain.class));
        evalModule(ai, type);
        if (Mvcs.DISPLAY_METHOD_LINENUMBER) {
            InputStream ins = type.getClassLoader().getResourceAsStream(type.getName().replace(".", "/") + ".class");
            if (ins != null) {
                try {
                    ClassMeta meta = ClassMetaReader.build(ins);
                    ai.setMeta(meta);
                }
                catch (Exception e) {
                }
            }
        }
        return ai;
    }

    public static ActionInfo createInfo(Method method) {
        ActionInfo ai = new ActionInfo();
        evalEncoding(ai, Mirror.getAnnotationDeep(method, Encoding.class));
        evalHttpAdaptor(ai, Mirror.getAnnotationDeep(method, AdaptBy.class));
        evalActionFilters(ai, Mirror.getAnnotationDeep(method, Filters.class));
        evalOk(ai, Mirror.getAnnotationDeep(method, Ok.class));
        evalFail(ai, Mirror.getAnnotationDeep(method, Fail.class));
        evalHttpMethod(ai, method, Mirror.getAnnotationDeep(method, At.class));
        evalAt(ai, Mirror.getAnnotationDeep(method, At.class), method.getName());
        evalActionChainMaker(ai, Mirror.getAnnotationDeep(method, Chain.class));
        ai.setMethod(method);
        return ai;
    }

    public static Set<Class<?>> scanModules(Ioc ioc, Class<?> mainModule) {
        Modules ann = mainModule.getAnnotation(Modules.class);
        boolean scan = null == ann ? true : ann.scanPackage();
        // 准备扫描列表
        Set<Class<?>> forScans = new HashSet<Class<?>>();

        // 准备存放模块类的集合
        Set<Class<?>> modules = new HashSet<Class<?>>();

        // 添加主模块，简直是一定的
        forScans.add(mainModule);

        // 根据配置，扩展扫描列表
        if (null != ann) {
            // 指定的类，这些类可以作为种子类，如果 ann.scanPackage 为 true 还要递归搜索所有子包
            for (Class<?> module : ann.value()) {
                forScans.add(module);
            }

            // 如果定义了扩展扫描接口 ...
            for (String str : ann.by()) {
                ModuleScanner ms;
                // 扫描器来自 Ioc 容器
                if (str.startsWith("ioc:")) {
                    String nm = str.substring("ioc:".length());
                    ms = ioc.get(ModuleScanner.class, nm);
                }
                // 扫描器直接无参创建
                else {
                    try {
                        Class<?> klass = Lang.loadClass(str);
                        Mirror<?> mi = Mirror.me(klass);
                        ms = (ModuleScanner) mi.born();
                    }
                    catch (ClassNotFoundException e) {
                        throw Lang.wrapThrow(e);
                    }
                }
                // 执行扫描，并将结果计入搜索结果
                Collection<Class<?>> col = ms.scan();
                if (null != col)
                    for (Class<?> type : col) {
                        if (isModule(type)) {
                            modules.add(type);
                        }
                    }
            }

            // 扫描包，扫描出的类直接计入结果
            if (ann.packages() != null && ann.packages().length > 0) {
                for (String packageName : ann.packages()) {
                    scanModuleInPackage(modules, packageName);
                }
            }
        }

        for (Class<?> type : forScans) {
            // mawm 为了兼容maven,根据这个type来加载该type所在jar的加载
            try {
                URL location = type.getProtectionDomain().getCodeSource().getLocation();
                if (log.isDebugEnabled())
                    log.debugf("module class location '%s'", location);
            }
            catch (NullPointerException e) {
                // Android上无法拿到getProtectionDomain,just pass
            }
            //Scans.me().registerLocation(type);
        }

        // 执行扫描
        for (Class<?> type : forScans) {
            // 扫描子包
            if (scan) {
                scanModuleInPackage(modules, type.getPackage().getName());
            }
            // 仅仅加载自己
            else {
                if (isModule(type)) {
                    if (log.isDebugEnabled())
                        log.debugf(" > Found @At : '%s'", type.getName());
                    modules.add(type);
                } else if (log.isTraceEnabled()) {
                    log.tracef(" > ignore '%s'", type.getName());
                }
            }
        }
        return modules;
    }

    protected static void scanModuleInPackage(Set<Class<?>> modules, String packageName) {
        if (log.isDebugEnabled())
            log.debugf(" > scan '%s'", packageName);

        List<Class<?>> subs = Scans.me().scanPackage(packageName);
        checkModule(modules, subs);
    }

    /**
     * @param modules
     * @param subs
     */
    private static void checkModule(Set<Class<?>> modules, List<Class<?>> subs) {
        for (Class<?> sub : subs) {
            try {
                if (isModule(sub)) {
                    if (log.isDebugEnabled())
                        log.debugf("   >> add '%s'", sub.getName());
                    modules.add(sub);
                } else if (log.isTraceEnabled()) {
                    log.tracef("   >> ignore '%s'", sub.getName());
                }
            }
            catch (Exception e) {
                throw new RuntimeException("something happen when handle class=" + sub.getName(), e);
            }
        }
    }

    public static void evalHttpMethod(ActionInfo ai, Method method, At at) {
        if (Mirror.getAnnotationDeep(method, GET.class) != null)
            ai.getHttpMethods().add("GET");
        if (Mirror.getAnnotationDeep(method, POST.class) != null)
            ai.getHttpMethods().add("POST");
        if (Mirror.getAnnotationDeep(method, PUT.class) != null)
            ai.getHttpMethods().add("PUT");
        if (Mirror.getAnnotationDeep(method, DELETE.class) != null)
            ai.getHttpMethods().add("DELETE");
        if (at != null) {
            for (String m : at.methods())
                ai.getHttpMethods().add(m.toUpperCase());
        }
    }

    public static void evalActionChainMaker(ActionInfo ai, Chain cb) {
        if (null != cb) {
            ai.setChainName(cb.value());
        }
    }

    public static void evalAt(ActionInfo ai, At at, String def) {
        if (null != at) {
            if (null == at.value() || at.value().length == 0) {
                ai.setPaths(Lang.array("/" + def.toLowerCase()));
            } else {
                ai.setPaths(at.value());
            }

            if (!Strings.isBlank(at.key()))
                ai.setPathKey(at.key());
            if (at.top())
                ai.setPathTop(true);
        } else if (!Lang.isEmpty(ai.getHttpMethods())) {
            // 没有@At但有GET POST等
            ai.setPaths(Lang.array("/" + def.toLowerCase()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void evalPathMap(ActionInfo ai, PathMap pathMap) {
        if (pathMap != null) {
            ai.setPathMap(Json.fromJson(Map.class, pathMap.value()));
        }
    }

    public static void evalFail(ActionInfo ai, Fail fail) {
        if (null != fail) {
            ai.setFailView(fail.value());
        }
    }

    public static void evalOk(ActionInfo ai, Ok ok) {
        if (null != ok) {
            ai.setOkView(ok.value());
        }
    }

    public static void evalModule(ActionInfo ai, Class<?> type) {
        ai.setModuleType(type);
        String beanName = null;
        // 按照5.10.3章节的说明，优先使用IocBean.name的注解声明bean的名字 Modify By QinerG@gmai.com
        InjectName innm = Mirror.getAnnotationDeep(type,InjectName.class);
        IocBean iocBean = Mirror.getAnnotationDeep(type,IocBean.class);
        if (innm == null && iocBean == null) // TODO 再考虑考虑
            return;
        if (iocBean != null) {
            beanName = iocBean.name();
        }
        if (Strings.isBlank(beanName)) {
            if (innm != null && !Strings.isBlank(innm.value())) {
                beanName = innm.value();
            } else {
                beanName = Strings.lowerFirst(type.getSimpleName());
            }
        }
        ai.setInjectName(beanName);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void evalActionFilters(ActionInfo ai, Filters filters) {
        if (null != filters) {
            List<ObjectInfo<? extends ActionFilter>> list = new ArrayList<ObjectInfo<? extends ActionFilter>>(filters.value().length);
            for (By by : filters.value()) {
                list.add(new ObjectInfo(by.type(), by.args()));
            }
            ai.setFilterInfos(list.toArray(new ObjectInfo[list.size()]));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void evalHttpAdaptor(ActionInfo ai, AdaptBy ab) {
        if (null != ab) {
            ai.setAdaptorInfo((ObjectInfo<? extends HttpAdaptor>) new ObjectInfo(ab.type(),
                                                                                 ab.args()));
        }
    }

    public static void evalEncoding(ActionInfo ai, Encoding encoding) {
        if (null == encoding) {
            ai.setInputEncoding(org.nutz.lang.Encoding.UTF8);
            ai.setOutputEncoding(org.nutz.lang.Encoding.UTF8);
        } else {
            ai.setInputEncoding(Strings.sNull(encoding.input(), org.nutz.lang.Encoding.UTF8));
            ai.setOutputEncoding(Strings.sNull(encoding.output(), org.nutz.lang.Encoding.UTF8));
        }
    }

    public static <T> T evalObj(NutConfig config, Class<T> type, String[] args) {
        // 用上下文替换参数
        Context context = config.getLoadingContext();
        for (int i = 0; i < args.length; i++) {
            args[i] = Segments.replace(args[i], context);
        }
        // 判断是否是 Ioc 注入

        if (args.length == 1 && args[0].startsWith("ioc:")) {
            String name = Strings.trim(args[0].substring(4));
            return config.getIoc().get(type, name);
        }
        return Mirror.me(type).born((Object[]) args);
    }

    public static boolean isModule(Class<?> classZ) {
        int classModify = classZ.getModifiers();
        if (!Modifier.isPublic(classModify)
            || Modifier.isAbstract(classModify)
            || Modifier.isInterface(classModify))
            return false;
        for (Method method : classZ.getMethods())
            if (Mirror.getAnnotationDeep(method, At.class) != null)
                return true;
        return false;
    }
}
