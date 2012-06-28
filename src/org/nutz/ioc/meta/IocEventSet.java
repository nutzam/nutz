package org.nutz.ioc.meta;

/**
 * 描述了一个对象可以监听的事件。
 * <p>
 * 三个属性分别表示：
 * <ul>
 * <li>create: 对象创建时触发
 * <li>fetch: 对象获取时触发
 * <li>depose: 对象销毁时触发
 * </ul>
 * 它们的值：
 * <ul>
 * <li>可以是一个函数名，也可以是一个 org.nutz.ioc.IocEventTrigger 的实现类全名
 * <li>如果 是函数，那么这个函数就是对象内的一个非静态 public 的函数，而且不能有参数
 * <li>如果是 IocEventTrigger 的实现类，你的实现类必须有一个 public 的默认构造函数
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.ioc.IocEventTrigger
 */
public class IocEventSet {

    private String create;

    private String fetch;

    private String depose;

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getFetch() {
        return fetch;
    }

    public void setFetch(String fetch) {
        this.fetch = fetch;
    }

    public String getDepose() {
        return depose;
    }

    public void setDepose(String depose) {
        this.depose = depose;
    }

}
