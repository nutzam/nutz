package org.nutz.ioc.impl;

import java.lang.reflect.Method;

import org.nutz.ioc.IocEventTrigger;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ObjectMaker;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.ValueProxy;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.trigger.MethodEventTrigger;
import org.nutz.ioc.weaver.DynamicWeaver;
import org.nutz.ioc.weaver.FieldInjector;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.born.Borning;

/**
 * 在这里，需要考虑 AOP
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ObjectMakerImpl implements ObjectMaker {

	public ObjectProxy make(IocMaking ing, IocObject iobj) {
		// 获取 Mirror， AOP 将在这个方法中进行
		Mirror<?> mirror = ing.getMirrors().getMirror(iobj.getType(), ing.getObjectName());

		/*
		 * 建立对象代理，并保存在上下文环境中 只有对象为 singleton 并且有一个非 null 的名称的时候才会保存
		 * 就是说，所有内部对象，将会随这其所附属的对象来保存，而自己不会单独保存
		 */
		ObjectProxy op = new ObjectProxy();
		if (iobj.isSingleton() && null != ing.getObjectName())
			ing.getContext().save(iobj.getScope(), ing.getObjectName(), op);

		// 解析对象的编织方式
		DynamicWeaver dw = new DynamicWeaver();

		// 建立对象的事件触发器
		if (null != iobj.getEvents()) {
			op.setFetch(createTrigger(mirror, iobj.getEvents().getFetch()));
			dw.setCreate(createTrigger(mirror, iobj.getEvents().getCreate()));
			dw.setDepose(createTrigger(mirror, iobj.getEvents().getDepose()));
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

		// 获得每个字段的注入方式
		FieldInjector[] fields = new FieldInjector[iobj.getFields().length];
		for (int i = 0; i < fields.length; i++) {
			IocField ifld = iobj.getFields()[i];
			try {
				ValueProxy vp = ing.makeValue(ifld.getValue());
				fields[i] = FieldInjector.create(mirror, ifld.getName(), vp);
			} catch (Exception e) {
				throw Lang.wrapThrow(e, "Fail to eval Injector for field: '%s'", ifld.getName());
			}
		}
		dw.setFields(fields);

		// 如果对象是 singleton, 那么转变成 static weaver
		if (iobj.isSingleton())
			op.setWeaver(dw.toStatic(ing));
		else
			op.setWeaver(dw);

		// 返回
		return op;
	}

	@SuppressWarnings("unchecked")
	private static IocEventTrigger<Object> createTrigger(Mirror<?> mirror, String str) {
		if (Strings.isBlank(str))
			return null;
		if (str.contains(".")) {
			try {
				Class<? extends IocEventTrigger> triggerType = (Class<? extends IocEventTrigger>) Class
						.forName(str);
				return triggerType.newInstance();
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}
		try {
			Method m = mirror.findMethod(str);
			return new MethodEventTrigger(m);
		} catch (NoSuchMethodException e) {
			throw Lang.wrapThrow(e);
		}

	}

}
