package org.nutz.dao;

import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 将一个参考对象存入 ThreadLocal
 * <p>
 * Nutz.Dao 将在构造 SQL 时，参考这个对象。如何参考，请参看 '@Table' 关于 “动态表名的赋值规则” 的描述
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class TableName {

    private static final Log log = Logs.get();

    private static final ThreadLocal<Object> object = new ThreadLocal<Object>();

    /**
     * 代码模板，这个模板保证了，在 atom 中运行的 POJO 的动态表名，都会被参考对象所影响
     * 
     * @param refer
     *            参考对象
     * @param atom
     *            你的业务逻辑
     */
    public static void run(Object refer, Runnable atom) {
        if (null != atom) {
            if (log.isTraceEnabled())
                log.tracef("TableName.run: [%s]->[%s]", object, object.get());

            Object old = get();
            set(refer);
            try {
                atom.run();
            }
            catch (Exception e) {
                throw Lang.wrapThrow(e);
            }
            finally {
                set(old);
                if (log.isTraceEnabled())
                    log.tracef("TableName.finally: [%s]->[%s]", object, object.get());
            }
        }
    }

    /**
     * @return 当前线程中的动态表名参考对象
     */
    public static Object get() {
        return object.get();
    }

    /**
     * 为当前线程设置动态表名参考对象
     * 
     * @param obj
     *            参考对象
     * @return 旧的动态表名参考对象
     */
    public static Object set(Object obj) {
        Object re = get();
        object.set(obj);
        return re;
    }

    /**
     * 清除当前线程的动态表名参考对象
     */
    public static void clear() {
        set(null);
    }

    /**
     * 根据当前线程的参考对象，渲染一个动态表名
     * 
     * @param tableName
     *            动态表名
     * @return 渲染后的表名
     */
    public static String render(Segment tableName) {
        Object obj = get();
        if (null == obj || !tableName.hasKey())
            return tableName.toString();

        Context context = Lang.context();
        if (isPrimitive(obj)) {
            for (String key : tableName.keys())
                context.set(key, obj);
        } else if (obj instanceof Context) {
            for (String key : tableName.keys())
                context.set(key, ((Context) obj).get(key));
        } else if (obj instanceof Map<?, ?>) {
            for (String key : tableName.keys())
                context.set(key, ((Map<?, ?>) obj).get(key));
        } else {
            Mirror<?> mirror = Mirror.me(obj);
            for (String key : tableName.keys())
                context.set(key, mirror.getValue(obj, key));
        }
        return tableName.render(context).toString();
    }

    public static boolean isPrimitive(Object obj) {
        return obj instanceof CharSequence || obj instanceof Number || obj.getClass().isPrimitive();
    }
}
