package org.nutz.mvc.loader.annotation;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.*;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.*;
import org.nutz.resource.Scans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 以注解为配置源，生成MVC需要的信息
 *
 * @author juqkai(juqkai@gmail.com)
 */
public class AnnotationModuleProvider implements ModuleProvider {
    private static final Log log = Logs.get();
    private Class<?> mainModule;
    private ActionInfo mainInfo;
    private NutConfig config;
    private Ioc ioc;
    private SessionProvider sessionProvider;
    private EntryDeterminer determiner;
    private UrlMapping mapping;

    public AnnotationModuleProvider(NutConfig config, Class<?> mainModule) {
        this.config = config;
        this.mainModule = mainModule;
    }

    public Ioc createIoc(){
        if (ioc != null) {
            return ioc;
        }
        IocBy ib = mainModule.getAnnotation(IocBy.class);
        if (null == ib) {
            log.info("!!!Your application without @IocBy supporting");
            return ioc;
        }
        if (log.isDebugEnabled())
            log.debugf("@IocBy(type=%s, args=%s,init=%s)",
                    ib.type().getName(),
                    Json.toJson(ib.args()),
                    Json.toJson(ib.init()));

        Ioc ioc = Mirror.me(ib.type()).born().create(config, ib.args());
        // 如果是 Ioc2 的实现，增加新的 ValueMaker
        if (ioc instanceof Ioc2) {
            ((Ioc2) ioc).addValueProxyMaker(new ServletValueProxyMaker(config.getServletContext()));
        }

        // 如果给定了 Ioc 的初始化，则依次调用
        for (String objName : ib.init()) {
            ioc.get(null, objName);
        }
        this.ioc = ioc;
        return ioc;
    }

    public List<Setup> getSetup(){
        List<Setup> setups = new ArrayList<>();
        SetupBy sb = mainModule.getAnnotation(SetupBy.class);
        if (null != sb) {
            if (log.isInfoEnabled())
                log.info("Setup application...");
            Setup setup = config.evalObj(sb.value(), sb.args());
            setups.add(setup);
        }
        if (Setup.class.isAssignableFrom(mainModule)) { // MainModule自己就实现了Setup接口呢?
            Setup setup = (Setup) Mirror.me(mainModule).born();
            setups.add(setup);
        }
        if (createIoc() != null) {
            String[] names = createIoc().getNames();
            Arrays.sort(names);
            boolean flag = true;
            for (String name : names) {
                if (name != null && name.startsWith(Setup.IOCNAME)) {
                    if (flag) {
                        flag = false;
                        if (log.isInfoEnabled())
                            log.info("Setup application...");
                    }
                    log.debug("load Setup from Ioc by name=" + name);
                    Setup setup = createIoc().get(Setup.class, name);
                    setups.add(setup);
                }
            }
        }
        return setups;
    }

    public List<ViewMaker> getViewMakers(){
        Views vms = mainModule.getAnnotation(Views.class);
        List<ViewMaker> makers = new ArrayList<ViewMaker>();
        if (null != vms) {
            for (int i = 0; i < vms.value().length; i++) {
                if (vms.value()[i].getAnnotation(IocBean.class) != null && ioc != null) {
                    makers.add(ioc.get(vms.value()[i]));
                } else {
                    makers.add(Mirror.me(vms.value()[i]).born());
                }
            }
        }
        return makers;
    }

    public ActionChainMaker getChainMaker() {
        ChainBy ann = mainModule.getAnnotation(ChainBy.class);
        ActionChainMaker maker = null == ann ? new NutActionChainMaker(new String[]{})
                : config.evalObj(ann.type(), ann.args());
        if (log.isDebugEnabled())
            log.debugf("@ChainBy(%s)", maker.getClass().getName());
        return maker;
    }


    public Loading createLoading() {
        /*
         * 获取 Loading
         */
        LoadingBy by = mainModule.getAnnotation(LoadingBy.class);
        if (null == by) {
            if (log.isDebugEnabled())
                log.debug("Loading by " + NutLoading.class);
            return new NutLoading();
        }
        try {
            if (log.isDebugEnabled())
                log.debug("Loading by " + by.value());
            return Mirror.me(by.value()).born();
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    @Override
    public Map<String, Map<String, Object>> getMessageSet() {
        Localization lc = mainModule.getAnnotation(Localization.class);
        if (null == lc) {
            // 否则记录一下
            if (log.isDebugEnabled()) {
                log.debug("@Localization not define");
            }
            return null;
        }
        if (log.isDebugEnabled())
            log.debugf("Localization: %s('%s') %s dft<%s>",
                    lc.type().getName(),
                    lc.value(),
                    Strings.isBlank(lc.beanName()) ? "" : "$ioc->" + lc.beanName(),
                    lc.defaultLocalizationKey());

        MessageLoader msgLoader = null;
        // 通过 Ioc 方式加载 MessageLoader ...
        if (!Strings.isBlank(lc.beanName())) {
            msgLoader = config.getIoc().get(lc.type(), lc.beanName());
        }
        // 普通方式加载
        else {
            msgLoader = Mirror.me(lc.type()).born();
        }
        // 加载数据
        return msgLoader.load(lc.value());

    }

    @Override
    public String getDefaultLocalizationKey() {
        Localization lc = mainModule.getAnnotation(Localization.class);
        if (null != lc) {
            // 如果有声明默认语言 ...
            if (!Strings.isBlank(lc.defaultLocalizationKey())){
                return lc.defaultLocalizationKey();
            }
        }
        // 否则记录一下
        else if (log.isDebugEnabled()) {
            log.debug("@Localization not define");
        }
        return null;
    }

    public SessionProvider getSessionProvider() {
        if(null != sessionProvider){
            return sessionProvider;
        }
        SessionBy sb = mainModule.getAnnotation(SessionBy.class);
        if (sb != null) {
            SessionProvider sp = null;
            if (sb.args() != null && sb.args().length == 1 && sb.args()[0].startsWith("ioc:"))
                sp = createIoc().get(sb.value(), sb.args()[0].substring(4));
            else
                sp = Mirror.me(sb.value()).born((Object[])sb.args());
            if (log.isInfoEnabled())
                log.info("SessionBy --> " + sp);
            sessionProvider = sp;
        }
        return sessionProvider;
    }

    public EntryDeterminer getDeterminer(){
        if (null != determiner) {
            return determiner;
        }
        Determiner ann = mainModule.getAnnotation(Determiner.class);
        determiner = null == ann ? new NutEntryDeterminer() : config.evalObj(ann.value(), ann.args());
        return determiner;
    }


    public UrlMapping getUrlMapping() {
        UrlMappingBy umb = mainModule.getAnnotation(UrlMappingBy.class);
        if (umb != null)
            return config.evalObj(umb.value(), umb.args());
        return new UrlMappingImpl();
    }

    public List<ActionInfo> loadActionInfos(){
        Modules ann = mainModule.getAnnotation(Modules.class);
        boolean scan = null == ann ? true : ann.scanPackage();
        // 准备存放模块类的集合
        Set<Class<?>> modules = new HashSet<Class<?>>();
        // 准备扫描列表
        Set<Class<?>> forScans = fetchModulesLevelClass(ann);
        if (!scan) {
            return scanModules(forScans);
        }
        // 扫描包
        Set<String> pns = new HashSet<>();
        if (null != ann) {
            // 扫描包，扫描出的类直接计入结果
            if (ann.packages() != null && ann.packages().length > 0) {
                pns.addAll(Lang.list(ann.packages()));
            }
        }
        for (Class<?> clazz : forScans) {
            pns.add(clazz.getPackage().getName());
        }
        return scanModuleInPackages(pns);
    }

    /**
     * 获取模块主类及配置过的所有主类
     * @param ann
     * @return
     */
    private Set<Class<?>> fetchModulesLevelClass(Modules ann){
        // 准备扫描列表
        Set<Class<?>> forScans = new HashSet<Class<?>>();
        // 添加主模块，简直是一定的
        forScans.add(mainModule);
        // 根据配置，扫描所有明确指定的class列表
        if (null == ann) {
            return forScans;
        }
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
                ms = config.getIoc().get(ModuleScanner.class, nm);
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
            if (null != col) {
                forScans.addAll(col);
            }
        }
        return forScans;
    }


    /**
     * 递归扫描包里所有类，找出所有的模块类
     * @param packageNames
     * @param determiner
     * @return
     */
    public List<ActionInfo> scanModuleInPackages(Set<String> packageNames){
        List<ActionInfo> modules = new ArrayList<>();
        for (String packageName : packageNames) {
            List<Class<?>> subs = Scans.me().scanPackage(packageName);
            modules.addAll(scanModules(subs));
        }
        return modules;
    }

    /**
     * 扫描类是否是模块类
     * @param clazzs
     * @param determiner
     * @return
     */
    public List<ActionInfo> scanModules(Collection<Class<?>> clazzs) {
        // 准备存放模块类的集合
        List<ActionInfo> modules = new ArrayList<>();
        // 执行扫描
        for (Class<?> type : clazzs) {
            try {
                if (!classIsModule(type)){
                    continue;
                }
                ActionInfo moduleInfo = null;
                for (Method method : type.getMethods()) {
                    if (!getDeterminer().isEntry(type, method)){
                        if (log.isTraceEnabled()) {
                            log.tracef("   >> ignore '%s'", type.getName());
                        }
                        continue;
                    }
                    if (moduleInfo == null) {
                        moduleInfo = ActionInfoCreator.createInfo(type).mergeWith(fetchMainInfo());
                    }
                    ActionInfo info = ActionInfoCreator.createInfo(method).mergeWith(moduleInfo);
                    modules.add(info);
                    if (log.isDebugEnabled()) {
                        log.debugf("   >> add '%s'", type.getName());
                    }
                }

                if (moduleInfo != null && null != moduleInfo.getPathMap()) {
                    for (Map.Entry<String, String> en : moduleInfo.getPathMap().entrySet()) {
                        config.getAtMap().add(en.getKey(), en.getValue());
                    }
                }
            }
            catch (Exception e) {
                throw new RuntimeException("something happen when handle class=" + type.getName(), e);
            }
        }
        return modules;
    }

    private ActionInfo fetchMainInfo(){
        if (mainInfo != null) {
            return mainInfo;
        }
        /*
         * 创建主模块的配置信息
         */
        ActionInfo mainInfo = ActionInfoCreator.createInfo(mainModule).mergeWith(fetchDefaultActionInfo());
        mainInfo.setInjectName(null);
        mainInfo.setModuleType(null);
        this.mainInfo = mainInfo;
        return mainInfo;
    }
    /**
     * 外部提供默认的一些ActionInfo配置
     * @return
     */
    protected  ActionInfo fetchDefaultActionInfo(){
        return null;
    }

    public boolean classIsModule(Class<?> classZ) {
        int classModify = classZ.getModifiers();
        if (!Modifier.isPublic(classModify)
                || Modifier.isAbstract(classModify)
                || Modifier.isInterface(classModify))
            return false;
        return true;
    }



}
