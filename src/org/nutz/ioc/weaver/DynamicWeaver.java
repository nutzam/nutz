package org.nutz.ioc.weaver;

import org.nutz.ioc.IocEventTrigger;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ObjectWeaver;
import org.nutz.ioc.ValueProxy;
import org.nutz.lang.born.Borning;

public class DynamicWeaver implements ObjectWeaver {

	private IocEventTrigger<Object> create;
	private IocEventTrigger<Object> depose;
	private Borning<?> borning;
	private ValueProxy[] args;
	private FieldInjector[] fields;

	public void setCreate(IocEventTrigger<Object> create) {
		this.create = create;
	}

	public void setDepose(IocEventTrigger<Object> depose) {
		this.depose = depose;
	}

	public void setBorning(Borning<?> borning) {
		this.borning = borning;
	}

	public void setArgs(ValueProxy[] args) {
		this.args = args;
	}

	public void setFields(FieldInjector[] fields) {
		this.fields = fields;
	}

	public void deose() {}

	public Object weave(IocMaking ing) {
		// 准备构造函数参数
		Object[] args = new Object[this.args.length];
		for (int i = 0; i < args.length; i++)
			args[i] = this.args[i].get(ing);

		// 创建实例
		Object obj = borning.born(args);

		// 设置字段的值
		for (FieldInjector fi : fields)
			fi.inject(ing, obj);

		// 触发创建事件
		if (null != create)
			create.trigger(obj);

		// 返回
		return obj;
	}

	public StaticWeaver toStatic(IocMaking ing) {
		Object obj = weave(ing);
		return new StaticWeaver(obj, depose);
	}
}
