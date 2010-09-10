package org.nutz.ioc.weaver;

import org.nutz.ioc.IocEventTrigger;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ObjectWeaver;
import org.nutz.ioc.ValueProxy;
import org.nutz.lang.born.Borning;

/**
 * 默认的对象编织过程
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class DefaultWeaver implements ObjectWeaver {

	/**
	 * 对象创建时的触发器
	 */
	protected IocEventTrigger<Object> create;

	/**
	 * 对象构造方法
	 */
	private Borning<?> borning;

	/**
	 * 对象构造方法参数
	 */
	private ValueProxy[] args;

	/**
	 * 字段注入器列表
	 */
	private FieldInjector[] fields;

	public void setCreate(IocEventTrigger<Object> create) {
		this.create = create;
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

	/**
	 * 根据容器构造时，为一个对象填充字段
	 * 
	 * @param ing
	 *            容器构造时
	 * @param obj
	 *            对象，要被填充字段
	 * 
	 * @return 被填充后的字段
	 */
	public <T> T fill(IocMaking ing, T obj) {
		// 设置字段的值
		for (FieldInjector fi : fields)
			fi.inject(ing, obj);
		return obj;
	}

	/**
	 * 根据自身内容创建一个对象，并触发创建事件
	 * 
	 * @param ing
	 *            容器构造时
	 */
	public Object born(IocMaking ing) {
		// 准备构造函数参数
		Object[] args = new Object[this.args.length];
		for (int i = 0; i < args.length; i++)
			args[i] = this.args[i].get(ing);

		// 创建实例
		Object obj = borning.born(args);

		// 触发创建事件
		if (null != create)
			create.trigger(obj);
		return obj;
	}
}
