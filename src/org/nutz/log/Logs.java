package org.nutz.log;

import org.nutz.log.impl.NopLog;
import org.nutz.plugin.SimplePluginManager;

/**
 * 获取 Log 的静态工厂方法
 * @author Young(sunonfire@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public final class Logs {

    private static LogAdapter adapter;

    static {
        init();
        try {
            get().info("Nutz is licensed under the Apache License, Version 2.0 .\nReport bugs : https://github.com/nutzam/nutz/issues");
        } catch (Throwable e) {
            // just pass!!
        }
    }

    /**
     * Get a Log by Class
     * 
     * @param clazz
     *            your class
     * @return Log
     */
    public static Log getLog(Class<?> clazz) {
        return getLog(clazz.getName());
    }

    /**
     * Get a Log by name
     * 
     * @param className
     *            the name of Log
     * @return Log
     */
    public static Log getLog(String className) {
        return adapter.getLogger(className);
    }

    /**
     * 返回以调用者的类命名的Log,是获取Log对象最简单的方法!
     */
    public static Log get() {
        return adapter.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    /**
     * 初始化NutLog,检查全部Log的可用性,选择可用的Log适配器
     * <p/>
     * <b>加载本类时,该方法已经在静态构造函数中调用,用户无需主动调用.</b>
     * <p/>
     * <b>除非迫不得已,请不要调用本方法<b/>
     * <p/>
     */
    public static void init() {
        try {
            String packageName = Logs.class.getPackage().getName() + ".impl.";
            adapter = new SimplePluginManager<LogAdapter>(    packageName + "Log4jLogAdapter",
                                                            packageName + "SystemLogAdapter").get();
        }
        catch (Throwable e) {
            //这是不应该发生的,SystemLogAdapter应该永远返回true
            //唯一的可能性是所请求的org.nutz.log.impl.SystemLogAdapter根本不存在
            //例如改了package
            e.printStackTrace();
        }
    }
    
    /**
     * 开放自定义设置LogAdapter,注意,不能设置为null!! 如果你打算完全禁用Nutz的log,可以设置为NOP_ADAPTER
     * @param adapter 你所偏好的LogAdapter
     */
    public static void setAdapter(LogAdapter adapter) {
		Logs.adapter = adapter;
	}
    
    /**
     * 什么都不做的适配器,无任何输出,某些人就想完全禁用掉NutzLog,就可以用上它了
     */
    public static LogAdapter NOP_ADAPTER = NopLog.NOP;
}
