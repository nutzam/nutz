package org.nutz.ioc;

import org.nutz.lang.Lang;

/**
 * 每次获取对象时会触发 fetch 事件，销毁时触发 depose 事件。
 * <p>
 * 这个对象需要小心被创建和使用。为了防止循环注入的问题，通常，ObjectMaker 需要快速<br>
 * 创建一个 ObjectProxy， 存入上下文。 然后慢慢的设置它的 weaver 和 fetch。
 * <p>
 * 在出现异常的时候，一定要将该对象从上下文中移除掉。
 *
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ObjectProxy {

    /**
     * 存储动态编织对象的方法
     */
    private ObjectWeaver weaver;

    /**
     * 存储静态对象
     */
    private Object obj;

    /**
     * 声明对象是否为单例。如果为单例，则在整个上下文环境下，只会有一份实例<br>
     * 内部对象的 singleton 将会被忽略
     */
    private boolean singleton;

    /**
     * 对象基本，容器根据这个字段，来决定将这个对象保存在哪一个上下文范围中<br>
     * 默认的为 "app"
     */
    private String scope;

    /**
     * 获取时触发器
     */
    private IocEventTrigger<Object> fetch;

    /**
     * 销毁时触发器。如果有静态对象被销毁，触发
     */
    private IocEventTrigger<Object> depose;

    public ObjectProxy() {}

    public ObjectProxy(Object obj) {
        this.obj = obj;
    }

    public ObjectProxy setWeaver(ObjectWeaver weaver) {
        this.weaver = weaver;
        return this;
    }

    public ObjectProxy setObj(Object obj) {
        this.obj = obj;
        return this;
    }

    public ObjectProxy setFetch(IocEventTrigger<Object> fetch) {
        this.fetch = fetch;
        return this;
    }

    public ObjectProxy setDepose(IocEventTrigger<Object> depose) {
        this.depose = depose;
        return this;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public ObjectProxy setSingleton(boolean singleton) {
        this.singleton = singleton;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public ObjectProxy setScope(String scope) {
        this.scope = scope;
        return this;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T get(Class<T> classOfT, IocMaking ing) {
        Object re;
        if (null != obj) {
            re = obj;
        } else if (null != weaver) {
            re = weaver.born(ing);
            if(singleton){
                obj = re;
            }
            re = weaver.onCreate(weaver.fill(ing, re));
        } else {
            throw Lang.makeThrow("NullProxy for '%s'!", ing.getObjectName());
        }

        if (null != fetch) {
            fetch.trigger(re);
        }

        return (T) re;
    }

    public void depose() {
        if (null != obj && null != depose)
            depose.trigger(obj);
    }

    public Object getObj() {
        return obj;
    }
}
