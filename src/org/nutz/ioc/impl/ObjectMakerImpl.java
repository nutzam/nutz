package org.nutz.ioc.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.*;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.weaver.DefaultWeaver;
import org.nutz.ioc.weaver.FieldInjector;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.born.Borning;
import org.nutz.lang.born.MethodBorning;
import org.nutz.lang.born.MethodCastingBorning;
import org.nutz.lang.reflect.FastClassFactory;
import org.nutz.lang.reflect.FastMethod;

/**
 * 在这里，需要考虑 AOP
 *
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class ObjectMakerImpl implements ObjectMaker {

    public ObjectProxy make(final IocMaking ing, IocObject iobj) {
        // 建立对象代理，并保存在上下文环境中 只有对象为 singleton
        // 并且有一个非 null 的名称的时候才会保存
        // 就是说，所有内部对象，将会随这其所附属的对象来保存，而自己不会单独保存
        ObjectProxy op = new ObjectProxy();
        op.setSingleton(iobj.isSingleton());
        op.setScope(iobj.getScope());
        op.setWeaver(makeWeaver(ing, iobj));
        // 为对象代理设置触发事件
        if (null != iobj.getEvents()) {
            op.setFetch(createTrigger(iobj.getEvents().getFetch()));
            op.setDepose(createTrigger(iobj.getEvents().getDepose()));
        }
        // 返回
        return op;
    }

    protected ObjectWeaver makeWeaver(final IocMaking ing, IocObject iobj){
        try {
            // 准备对象的编织方式
            DefaultWeaver dw = new DefaultWeaver();
            dw.setListeners(ing.getListeners());
            // 构造函数参数
            ValueProxy[] vps = new ValueProxy[Lang.eleSize(iobj.getArgs())];
            for (int i = 0; i < vps.length; i++)
                vps[i] = ing.makeValue(iobj.getArgs()[i]);
            dw.setArgs(vps);

            // 先获取一遍，根据这个数组来获得构造函数
            Object[] args = new Object[vps.length];
            boolean hasNullArg = false;
            for (int i = 0; i < args.length; i++) {
                args[i] = vps[i].get(ing);
                if (args[i] == null) {
                    hasNullArg = true;
                }
            }
            // 获取 Mirror， AOP 将在这个方法中进行
            Mirror<?> mirror = null;

            // 缓存构造函数
            if (iobj.getFactory() != null) {
                // factory这属性, 格式应该是 类名#方法名 或者 $iocbean#方法名
                final String[] ss = iobj.getFactory().split("#", 2);
                if (ss[0].startsWith("$")) {
                    dw.setBorning(new Borning<Object>() {
                        public Object born(Object... args) {
                            Object factoryBean = ing.getIoc().get(null, ss[0].substring(1));
                            return Mirror.me(factoryBean).invoke(factoryBean, ss[1], args);
                        }
                    });
                } else {
                    Mirror<?> mi = Mirror.me(Lang.loadClass(ss[0]));
                    Method m;
                    if (hasNullArg) {
                        m = (Method) Lang.first(mi.findMethods(ss[1],args.length));
                        if (m == null)
                            throw new IocException(ing.getObjectName(), "Factory method not found --> ", iobj.getFactory());
                        dw.setBorning(new MethodCastingBorning<Object>(m));
                    } else {
                        m = mi.findMethod(ss[1], args);
                        dw.setBorning(new MethodBorning<Object>(m));
                    }
                    if (iobj.getType() == null)
                        iobj.setType(m.getReturnType());
                }
                if (iobj.getType() != null)
                    mirror = ing.getMirrors().getMirror(iobj.getType(), ing.getObjectName());
            } else {
                mirror = ing.getMirrors().getMirror(iobj.getType(), ing.getObjectName());
                dw.setBorning((Borning<?>) mirror.getBorning(args));
            }

            // 获得每个字段的注入方式
            List<IocField> _fields = new ArrayList<IocField>(iobj.getFields().values());
            FieldInjector[] fields = new FieldInjector[_fields.size()];
            for (int i = 0; i < fields.length; i++) {
                IocField ifld = _fields.get(i);
                try {
                    ValueProxy vp = ing.makeValue(ifld.getValue());
                    fields[i] = FieldInjector.create(mirror, ifld.getName(), vp, ifld.isOptional());
                }
                catch (Exception e) {
                    throw Lang.wrapThrow(e, "Fail to eval Injector for field: '%s'", ifld.getName());
                }
            }
            dw.setFields(fields);
            // 为对象代理设置触发事件
            if (null != iobj.getEvents()) {
                dw.setCreate(createTrigger(iobj.getEvents().getCreate()));
            }
            return dw;
        }
        catch (IocException e) {
            ing.getContext().remove(iobj.getScope(), ing.getObjectName());
            ((IocException)e).addBeanNames(ing.getObjectName());
            throw e;
        }
        // 当异常发生，从 context 里移除 ObjectProxy
        catch (Throwable e) {
            ing.getContext().remove(iobj.getScope(), ing.getObjectName());
            throw new IocException(ing.getObjectName(), e, "throw Exception when creating");
        }
    }

    @SuppressWarnings({"unchecked"})
    private static IocEventTrigger<Object> createTrigger(final String str) {
        if (Strings.isBlank(str))
            return null;
        if (str.contains(".")) {
            try {
                return (IocEventTrigger<Object>) Mirror.me(Lang.loadClass(str))
                                                       .born();
            }
            catch (Exception e) {
                throw Lang.wrapThrow(e);
            }
        }
        return new IocEventTrigger<Object>() {
        	protected FastMethod fm;
			public void trigger(Object obj) {
				try {
					if (fm == null) {
						Method method = Mirror.me(obj).findMethod(str);
						fm = FastClassFactory.get(method);
					}
					fm.invoke(obj);
				} catch (Exception e) {
					throw Lang.wrapThrow(e);
				}
			}
        };
    }

}
