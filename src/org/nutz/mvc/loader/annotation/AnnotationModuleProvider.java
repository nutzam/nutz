package org.nutz.mvc.loader.annotation;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.json.Json;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.MessageLoader;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.impl.ModuleProvider;
import org.nutz.mvc.impl.ServletValueProxyMaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AnnotationModuleProvider implements ModuleProvider {
    private static final Log log = Logs.get();
    private Class<?> mainModule;
    private NutConfig config;
    private Ioc ioc;

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
            Setup setup = NutConfig.evalObj(config, sb.value(), sb.args());
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

}
