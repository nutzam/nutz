package org.nutz.ioc.impl;

import java.util.ArrayList;

import org.nutz.ioc.IocContext;
import org.nutz.ioc.ObjectProxy;

/**
 * 组合了一组 IocContext。每当保存（save）时，会存入所有的Context。
 * <p>
 * 每当获取时 按照构造Context的顺序，依次获取。 只要有一个 Context 返回了非 null 对象，就立即返回
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ComboContext implements IocContext {

    private IocContext[] contexts;

    /**
     * Context 的获取优先级，以数组的顺序来决定
     * 
     * @param contexts
     */
    public ComboContext(IocContext... contexts) {
        ArrayList<IocContext> tmp = new ArrayList<IocContext>(contexts.length);
        for (IocContext iocContext : contexts) {
            if (tmp.contains(iocContext))
                continue;
            if (iocContext instanceof ComboContext){
                ComboContext comboContext = (ComboContext)iocContext;
                for (IocContext iocContext2 : comboContext.contexts) {
                    if (tmp.contains(iocContext2))
                        continue;
                    tmp.add(iocContext2);
                }
            }
            else
                tmp.add(iocContext);
        }
        this.contexts = tmp.toArray(new IocContext[tmp.size()]);
    }

    public ObjectProxy fetch(String key) {
        for (IocContext c : contexts) {
            ObjectProxy re = c.fetch(key);
            if (null != re)
                return re;
        }
        return null;
    }

    public boolean save(String scope, String name, ObjectProxy obj) {
        boolean re = false;
        for (IocContext c : contexts)
            re &= c.save(scope, name, obj);
        return re;
    }

    public boolean remove(String scope, String name) {
        boolean re = false;
        for (IocContext c : contexts)
            re &= c.remove(scope, name);
        return re;
    }

    public void clear() {
        for (IocContext c : contexts)
            c.clear();
    }

    public void depose() {
        for (IocContext c : contexts)
            c.depose();
    }

    public IocContext[] getContexts() {
        return contexts;
    }
}
