package org.nutz.ioc.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.nutz.ioc.IocMaking;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

/**
 * 静态方法或静态字段节点
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class StaticFunctionNode extends ChainNode {

    private Method method;
    private ChainNode[] args;
    
    private Field field;

    public StaticFunctionNode(String className, String name, ChainNode[] args) {
        try {
            Mirror<?> mirror = Mirror.me(Lang.loadClass(className));
            if (null == args || args.length == 0) {
                try {
                    method = mirror.getGetter(name);
                    if (!Modifier.isStatic(method.getModifiers()))
                        throw Lang.makeThrow("Method '%s' of '%s' must be static", name, mirror);
                }
                catch (NoSuchMethodException e) {
                    try {
                        field = mirror.getField(name);
                        if (!Modifier.isStatic(field.getModifiers()))
                            throw Lang.makeThrow("Field '%s' of '%s' must be static", name, mirror);
                        return;
                    } catch (NoSuchFieldException e1) {
                        throw Lang.makeThrow("Method or field '%s' don't find in '%s'", name, mirror);
                    }
                }
            } else {
                Method[] ms = mirror.findMethods(name, args.length);
                if (0 != ms.length)
                    for (int i = 0; i < ms.length; i++)
                        if(Modifier.isStatic(ms[i].getModifiers())) {
                                method = ms[i];
                                break;
                        }
                if (method == null)
                    throw Lang.makeThrow("Method '%s' don't find in '%s' or it is NOT static", name, mirror);
                this.args = args;
            }
        }
        catch (ClassNotFoundException e) {
            throw Lang.wrapThrow(e);
        }
    }

    protected Object getValue(IocMaking ing, Object obj) throws Exception {
        if (method != null){
            if (null == args || args.length == 0)
                return method.invoke(obj);
            Object[] fas = new Object[args.length];
            for (int i = 0; i < args.length; i++)
                fas[i] = args[i].getValue(ing, null);
            return method.invoke(obj, fas);
        }
        return field.get(null);
    }

    protected String asString() {
        StringBuilder sb = new StringBuilder();
        if (null != args && args.length > 0) {
            sb.append(args[0].toString());
            for (int i = 1; i < args.length; i++)
                sb.append(", ").append(args[i].toString());
        }
        if (method != null)
            return String.format(    "%s.%s(%s)",
                                method.getDeclaringClass().getName(),
                                method.getName(),
                                sb);
        else
            return String.format(    "%s.%s(%s)",
                    field.getDeclaringClass().getName(),
                    field.getName(),
                    sb);
    }

}
