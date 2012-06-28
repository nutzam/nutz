package org.nutz.ioc.meta;

import java.util.ArrayList;
import java.util.List;

import org.nutz.json.Json;

/**
 * 描述了对象的注入信息
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * 
 * @see org.nutz.ioc.meta.IocEventSet
 * @see org.nutz.ioc.meta.IocValue
 * @see org.nutz.ioc.meta.IocField
 */
public class IocObject implements Cloneable {

    /**
     * 对象类型，如果为 null，则使用 Ioc 接口函数的第一个参数作为本次获取的类型。
     */
    private Class<?> type;

    /**
     * 声明对象是否为单例。如果为单例，则在整个上下文环境下，只会有一份实例<br>
     * 内部对象的 singleton 将会被忽略
     */
    private boolean singleton;

    /**
     * 对象监听何种事件
     */
    private IocEventSet events;

    /**
     * 对象构造函数的参数列表
     */
    private List<IocValue> args;

    /**
     * 对象的字段
     */
    private List<IocField> fields;

    /**
     * 对象基本，容器根据这个字段，来决定将这个对象保存在哪一个上下文范围中<br>
     * 默认的为 "app"
     */
    private String scope;

    public IocObject() {
        args = new ArrayList<IocValue>();
        fields = new ArrayList<IocField>();
        singleton = true;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public IocEventSet getEvents() {
        return events;
    }

    public void setEvents(IocEventSet events) {
        this.events = events;
    }

    public IocValue[] getArgs() {
        return args.toArray(new IocValue[args.size()]);
    }

    public boolean hasArgs() {
        return args.size() > 0;
    }

    public void addArg(IocValue arg) {
        this.args.add(arg);
    }

    public void copyArgys(IocValue[] args) {
        this.args.clear();
        for (IocValue arg : args) {
            addArg(arg);
        }
    }

    public IocField[] getFields() {
        return fields.toArray(new IocField[fields.size()]);
    }

    public void addField(IocField field) {
        this.fields.add(field);
    }

    public boolean hasField(String name) {
        for (IocField fld : fields)
            if (fld.getName().equals(name))
                return true;
        return false;
    }

    public IocObject clone() {
        return Json.fromJson(IocObject.class, Json.toJson(this));
    }
    
    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
