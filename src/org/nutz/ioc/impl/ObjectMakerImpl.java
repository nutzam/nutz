package org.nutz.ioc.impl;

import org.nutz.ioc.IocEventTrigger;
import org.nutz.ioc.IocException;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ObjectMaker;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.ValueProxy;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.trigger.MethodEventTrigger;
import org.nutz.ioc.weaver.DefaultWeaver;
import org.nutz.ioc.weaver.FieldInjector;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.born.Borning;

/**
 * 在这里，需要考虑 AOP
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class ObjectMakerImpl implements ObjectMaker {

    public ObjectProxy make(IocMaking ing, IocObject iobj) {
        // 获取 Mirror， AOP 将在这个方法中进行
        Mirror<?> mirror = ing.getMirrors().getMirror(iobj.getType(), ing.getObjectName());

        // 获取配置的对象事件集合
        IocEventSet iocEventSet = iobj.getEvents();

        // 建立对象代理，并保存在上下文环境中 只有对象为 singleton
        // 并且有一个非 null 的名称的时候才会保存
        // 就是说，所有内部对象，将会随这其所附属的对象来保存，而自己不会单独保存
        ObjectProxy op = new ObjectProxy();
        if (iobj.isSingleton() && null != ing.getObjectName())
            ing.getContext().save(iobj.getScope(), ing.getObjectName(), op);

        // 为对象代理设置触发事件
        if (null != iobj.getEvents()) {
            op.setFetch(createTrigger(mirror, iocEventSet.getFetch()));
            op.setDepose(createTrigger(mirror, iocEventSet.getDepose()));
        }

        try {
            // 准备对象的编织方式
            DefaultWeaver dw = new DefaultWeaver();
            op.setWeaver(dw);

            // 为编织器设置事件触发器：创建时
            if (null != iobj.getEvents()) {
                dw.setCreate(createTrigger(mirror, iocEventSet.getCreate()));
            }

            // 构造函数参数
            ValueProxy[] vps = new ValueProxy[Lang.length(iobj.getArgs())];
            for (int i = 0; i < vps.length; i++)
                vps[i] = ing.makeValue(iobj.getArgs()[i]);
            dw.setArgs(vps);

            // 先获取一遍，根据这个数组来获得构造函数
            Object[] args = new Object[vps.length];
            for (int i = 0; i < args.length; i++)
                args[i] = vps[i].get(ing);

            // 缓存构造函数
            dw.setBorning((Borning<?>) mirror.getBorning(args));

            // 如果这个对象是容器中的单例，那么就可以生成实例了
            // 这一步非常重要，它解除了字段互相引用的问题
            Object obj = null;
            if (iobj.isSingleton()) {
                obj = dw.born(ing);
                op.setObj(obj);
            }

            // 获得每个字段的注入方式
            FieldInjector[] fields = new FieldInjector[iobj.getFields().length];
            for (int i = 0; i < fields.length; i++) {
                IocField ifld = iobj.getFields()[i];
                try {
                    ValueProxy vp = ing.makeValue(ifld.getValue());
                    fields[i] = FieldInjector.create(mirror, ifld.getName(), vp);
                }
                catch (Exception e) {
                    throw Lang.wrapThrow(e, "Fail to eval Injector for field: '%s'", ifld.getName());
                }
            }
            dw.setFields(fields);

            // 如果是单例对象，前面已经生成实例了，在这里需要填充一下它的字段
            if (null != obj)
                dw.fill(ing, obj);

            // 对象创建完毕，如果有 create 事件，调用它
            dw.onCreate(obj);

        }
        // 当异常发生，从 context 里移除 ObjectProxy
        catch (Throwable e) {
            ing.getContext().remove(iobj.getScope(), ing.getObjectName());
            throw Lang.wrapThrow(e, IocException.class);
        }

        // 返回
        return op;
    }

    @SuppressWarnings({"unchecked"})
    private static IocEventTrigger<Object> createTrigger(Mirror<?> mirror, String str) {
        if (Strings.isBlank(str))
            return null;
        if (str.contains(".")) {
            try {
                return (IocEventTrigger<Object>) Mirror.me(Lang.loadClass(str)).born();
            }
            catch (Exception e) {
                throw Lang.wrapThrow(e);
            }
        }
        try {
            return new MethodEventTrigger(mirror.findMethod(str));
        }
        catch (NoSuchMethodException e) {
            throw Lang.wrapThrow(e);
        }
    }

}
