package org.nutz.el.opt.custom;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.nutz.el.ElException;
import org.nutz.el.opt.RunMethod;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.plugin.Plugin;

/**
 * 间接调用一个RunMethod类(必须有无参构造方法)或指定类的指定方法(如果不是静态方法,则必须带无参构造方法)<p/>
 * 
 * 调用示例 <p/>
 * <code>@El("by('net.wendal.util.FuckId')")</code><p/>
 * <code>@El("by('net.wendal.util.FuckId#staticFunc')")</code><p/>
 * <code>@El("by('net.wendal.util.FuckId#make')")</code><p/>
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ByMake implements RunMethod, Plugin{

    public boolean canWork() {
        return true;
    }

    public Object run(List<Object> fetchParam) {
        if (fetchParam.isEmpty())
            throw new ElException("'by' must have params");
        String p = (String)fetchParam.remove(0);
        String className = p;
        String methodName = null;
        if (p.contains("#")) {
            String[] tmp = p.split("#");
            className = tmp[0];
            methodName = tmp[1];
        }
        try {
            Class<?> klass = Lang.loadClass(className);
            if (methodName == null) {
                methodName = "run";
            }
            if (RunMethod.class.isAssignableFrom(klass)) {
                return ((RunMethod)klass.newInstance()).run(fetchParam);
            }
            Object[] args = fetchParam.toArray();
            Method method = Mirror.me(klass).findMethod(methodName, args);
            if (Modifier.isStatic(method.getModifiers())) {
                return method.invoke(null, args);
            } else {
                return method.invoke(klass.newInstance(), args);
            }
        }
        catch (Exception e) {
            throw new ElException(e);
        }
    }

    public String fetchSelf() {
        return "by";
    }

}
