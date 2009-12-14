package org.nutz.ioc.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocContext;
import org.nutz.ioc.IocException;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.ObjectMaker;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.ValueProxyMaker;
import org.nutz.ioc.aop.MirrorFactory;
import org.nutz.ioc.aop.impl.DefaultMirrorFactory;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class NutIoc implements Ioc2 {

	private static Log log = Logs.getLog(NutIoc.class);

	private static final String DEF_SCOPE = "app";

	private IocLoader loader;
	private IocContext context;
	private ObjectMaker maker;
	private List<ValueProxyMaker> vpms;
	private MirrorFactory mirrors;
	private String defaultScope;

	public NutIoc(IocLoader loader) {
		this(loader, new ScopeContext(DEF_SCOPE), DEF_SCOPE);
	}

	public NutIoc(IocLoader loader, IocContext context, String defaultScope) {
		this(new ObjectMakerImpl(), loader, context, defaultScope);
	}

	protected NutIoc(ObjectMaker maker, IocLoader loader, IocContext context, String defaultScope) {
		this.defaultScope = defaultScope;
		this.loader = loader;
		this.context = context;
		this.maker = maker;
		vpms = new ArrayList<ValueProxyMaker>(5); // 预留五个位置，足够了吧
		addValueProxyMaker(new DefaultValueProxyMaker());

		// 初始化类工厂， 这是同 AOP 的连接点
		mirrors = new DefaultMirrorFactory(this);
	}

	public <T> T get(Class<T> type, String name, IocContext context) throws IocException {
		if (log.isDebugEnabled())
			log.debugf("Get '%s'<%s>", name, type);

		// 连接上下文
		IocContext cntx;
		if (null == context)
			cntx = this.context;
		else {
			if (log.isTraceEnabled())
				log.trace("Link contexts");
			cntx = new ComboContext(context, this.context);
		}

		// 创建对象创建时
		IocMaking ing = new IocMaking(this, mirrors, cntx, maker, vpms, name);

		// 从上下文缓存中获取对象代理
		ObjectProxy re = cntx.fetch(name);

		// 如果未发现对象
		if (null == re) {
			// 线程同步
			synchronized (this) {
				// 再次读取
				re = cntx.fetch(name);

				// 如果未发现对象
				if (null == re) {
					try {
						if (log.isDebugEnabled())
							log.debug("\t >> Load definition");

						// 读取对象定义
						IocObject iobj = loader.load(name);
						if (null == iobj)
							throw Lang.makeThrow("Undefined object '%s'", name);

						// 修正对象类型
						if (null == iobj.getType())
							if (null == type)
								throw Lang.makeThrow("NULL TYPE object '%s'", name);
							else
								iobj.setType(type);

						// 检查对象级别
						if (Strings.isBlank(iobj.getScope()))
							iobj.setScope(defaultScope);

						// 根据对象定义，创建对象，maker 会自动的缓存对象到 context 中
						if (log.isDebugEnabled())
							log.debug("\t >> Make...");
						re = maker.make(ing, iobj);
					}
					// 处理异常
					catch (ObjectLoadException e) {
						throw new IocException(e, "For object [%s] - type:[%s]", name, type);
					}
				}
			}
		}
		return (T) re.get(type, ing);
	}

	public <T> T get(Class<T> type, String name) {
		return this.get(type, name, null);
	}

	public boolean has(String name) {
		return loader.has(name);
	}

	public void depose() {
		context.depose();
		if (log.isDebugEnabled())
			log.debug("!!!Ioc is deposed, you can't use it anymore");
	}

	public void reset() {
		context.clear();
	}

	public String[] getName() {
		return loader.getName();
	}

	public void addValueProxyMaker(ValueProxyMaker vpm) {
		vpms.add(vpm);
	}

	public void setMaker(ObjectMaker maker) {
		this.maker = maker;
	}

}
