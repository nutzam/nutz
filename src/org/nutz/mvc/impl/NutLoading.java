package org.nutz.mvc.impl;

import org.nutz.Nutz;
import org.nutz.ioc.Ioc;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.*;
import org.nutz.mvc.view.DefaultViewMaker;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

public class NutLoading implements Loading {

    private static final Log log = Logs.get();
    protected NutConfig config;
    protected UrlMapping mapping = null;
    protected ModuleProvider moduleProvider = null;

    public UrlMapping load(NutConfig config) {
        if (log.isInfoEnabled()) {
            log.infof("Nutz Version : %s ", Nutz.version());
            log.infof("Nutz.Mvc[%s] is initializing ...", config.getAppName());
        }
        if (log.isDebugEnabled()) {
            Properties sys = System.getProperties();
            log.debug("Web Container Information:");
            log.debugf(" - Default Charset : %s", Encoding.defaultEncoding());
            log.debugf(" - Current . path  : %s", new File(".").getAbsolutePath());
            log.debugf(" - Java Version    : %s", sys.get("java.version"));
            log.debugf(" - File separator  : %s", sys.get("file.separator"));
            log.debugf(" - Timezone        : %s", sys.get("user.timezone"));
            log.debugf(" - OS              : %s %s", sys.get("os.name"), sys.get("os.arch"));
            log.debugf(" - ServerInfo      : %s", config.getServletContext().getServerInfo());
            log.debugf(" - Servlet API     : %d.%d",
                    config.getServletContext().getMajorVersion(),
                    config.getServletContext().getMinorVersion());
            if (config.getServletContext().getMajorVersion() > 2
                    || config.getServletContext().getMinorVersion() > 4)
                log.debugf(" - ContextPath     : %s", config.getServletContext().getContextPath());
            log.debugf(" - context.tempdir : %s", config.getAttribute("javax.servlet.context.tempdir"));
            log.debugf(" - MainModule      : %s", config.getMainModulePackage());
        }
        this.config = config;
        /*
         * 准备返回值
         */
        Ioc ioc = null;

        /*
         * 准备计时
         */
        Stopwatch sw = Stopwatch.begin();

        try {
            /*
             * 创建上下文
             */
            createContext(config);

            /*
             * 检查 Ioc 容器并创建和保存它
             */
            moduleProvider = config.getModuleProvider();
            ioc = moduleProvider.createIoc();
            // 保存 Ioc 对象
            Mvcs.setIoc(ioc);


            /*
             * 组装UrlMapping
             */
            mapping = evalUrlMapping(config);

            /*
             * 分析本地化字符串
             */
            evalLocalization(config);

            /*
             * 执行用户自定义 Setup
             */
            evalSetup(config);

            // 应用完成后执行用户自定义的 CommandLineRunner
            callRunners(ioc);
        }
        catch (Exception e) {
            if (log.isErrorEnabled())
                log.error("Error happend during start serivce!", e);
            if (ioc != null) {
                log.error("try to depose ioc");
                try {
                    ioc.depose();
                }
                catch (Throwable e2) {
                    log.error("error when depose ioc", e);
                }
            }
            throw Lang.wrapThrow(e, LoadingException.class);
        }

        // ~ Done ^_^
        sw.stop();
        if (log.isInfoEnabled())
            log.infof("Nutz.Mvc[%s] is up in %sms", config.getAppName(), sw.getDuration());

        return mapping;

    }

    protected UrlMapping evalUrlMapping(NutConfig config) throws Exception {
        /*
         * 创建视图工厂
         */
        List<ViewMaker> makers = createViewMakers();
        Mvcs.ctx().setViewMakers(makers);

        /*
         * 创建动作链工厂
         */
//        ActionChainMaker maker = moduleProvider.getChainMaker();
        Mvcs.ctx().setActionChainMaker(moduleProvider.getChainMaker());

        /*
         * 准备 UrlMapping
         */
        mapping = moduleProvider.getUrlMapping();
        Mvcs.ctx().setUrlMapping(mapping);
        if (log.isInfoEnabled())
            log.infof("Build URL mapping by %s ...", mapping.getClass().getName());

        /*
         * 准备要加载的模块列表
         */
        List<ActionInfo> actionInfos = moduleProvider.loadActionInfos();
        return putActionInfo(actionInfos);
    }

    protected UrlMapping putActionInfo(List<ActionInfo> actionInfos){
        for (ActionInfo ai : actionInfos) {
            ai.setViewMakers(Mvcs.ctx().getViewMakers());
            mapping.add(Mvcs.ctx().getActionChainMaker(), ai, config);
        }

        if (actionInfos.size() == 0) {
            if (log.isWarnEnabled())
                log.warn("None @At found in any modules class!!");
        } else {
            log.infof("Found %d module methods", actionInfos.size());
        }
        return mapping;
    }

    protected void createContext(NutConfig config) {
        // 构建一个上下文对象，方便子类获取更多的环境信息
        // 同时，所有 Filter 和 Adaptor 都可以用 ${app.root} 来填充自己
        Context context = Lang.context();
        String appRoot = config.getAppRoot();
        context.set("app.root", appRoot);

        if (log.isDebugEnabled()) {
            log.debugf(">> app.root = %s", appRoot);
        }

        // 载入环境变量
        for (Entry<String, String> entry : System.getenv().entrySet())
            context.set("env." + entry.getKey(), entry.getValue());
        // 载入系统变量
        for (Entry<Object, Object> entry : System.getProperties().entrySet())
            context.set("sys." + entry.getKey(), entry.getValue());

        if (log.isTraceEnabled()) {
            log.tracef(">>\nCONTEXT %s", Json.toJson(context, JsonFormat.nice()));
        }
        config.getServletContext().setAttribute(Loading.CONTEXT_NAME, context);
    }

    protected void evalSetup(NutConfig config) throws Exception {
        List<Setup> setups = moduleProvider.getSetup();
        for (Setup setup : setups) {
            config.setAttributeIgnoreNull(Setup.class.getName(), setup);
            setup.init(config);
        }
    }

    protected void callRunners(Ioc ioc){
        if(Objects.nonNull(ioc)){
            String[] names = ioc.getNamesByType(CommandLineRunner.class);
            Arrays.sort(names);
            for (String beanName : names) {
                CommandLineRunner commandLineRunner = ioc.get(CommandLineRunner.class, beanName);
                callRunner(commandLineRunner);
            }
        }
    }

    private void callRunner(CommandLineRunner runner){
        try {
            runner.run();
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failed to execute CommandLineRunner", ex);
        }
    }

    protected void evalLocalization(NutConfig config) {
        // 保存消息 Map
        Mvcs.setMessageSet(moduleProvider.getMessageSet());

        // 如果有声明默认语言 ...
        if (!Strings.isBlank(moduleProvider.getDefaultLocalizationKey())) {
            Mvcs.setDefaultLocalizationKey(moduleProvider.getDefaultLocalizationKey());
        }
    }

    protected List<ViewMaker> createViewMakers() throws Exception {
        List<ViewMaker> makers = new ArrayList<ViewMaker>();
        makers.addAll(moduleProvider.getViewMakers());
        Ioc ioc = moduleProvider.createIoc();
        if (ioc != null) {
            String[] names = ioc.getNames();
            Arrays.sort(names);
            for (String name : ioc.getNames()) {
                if (name != null && name.startsWith(ViewMaker.IOCNAME)) {
                    log.debug("add ViewMaker from Ioc by name=" + name);
                    makers.add(ioc.get(ViewMaker.class, name));
                }
            }
        }
        makers.add(new DefaultViewMaker());// 优先使用用户自定义

        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            for (ViewMaker maker : makers) {
                sb.append(maker.getClass().getSimpleName()).append(".class,");
            }
            sb.setLength(sb.length() - 1);
            log.debugf("@Views(%s)", sb);
        }

        return makers;
    }

    public void depose(NutConfig config) {
        if (log.isInfoEnabled())
            log.infof("Nutz.Mvc[%s] is deposing ...", config.getAppName());
        Stopwatch sw = Stopwatch.begin();

        // Firstly, upload the user customized desctroy
        try {
            Setup setup = config.getAttributeAs(Setup.class, Setup.class.getName());
            if (null != setup)
                setup.destroy(config);
        }
        catch (Exception e) {
            throw new LoadingException(e);
        }
        finally {
            SessionProvider sp = config.getSessionProvider();
            if (sp != null)
                sp.notifyStop();
            // If the application has Ioc, depose it
            Ioc ioc = config.getIoc();
            if (null != ioc)
                ioc.depose();
        }

        // Done, print info
        sw.stop();
        if (log.isInfoEnabled())
            log.infof("Nutz.Mvc[%s] is down in %sms", config.getAppName(), sw.getDuration());
    }


    @Override
    public ActionInvoker fetch(ActionContext ac) {
        return mapping.get(ac);
    }

}
