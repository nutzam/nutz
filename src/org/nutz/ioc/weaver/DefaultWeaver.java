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
    private IocEventTrigger<Object> create;

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

    public <T> T fill(IocMaking ing, T obj) {
        // 设置字段的值
        for (FieldInjector fi : fields)
            fi.inject(ing, obj);
        return obj;
    }

    public Object born(IocMaking ing) {
        // 准备构造函数参数
        Object[] args = new Object[this.args.length];
        for (int i = 0; i < args.length; i++)
            args[i] = this.args[i].get(ing);

        // 创建实例
        Object obj = borning.born(args);

        return obj;
    }

    public Object onCreate(Object obj) {
        if (null != create && null != obj)
            create.trigger(obj);
        return obj;
    }
}
