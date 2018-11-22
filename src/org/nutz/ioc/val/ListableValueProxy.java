package org.nutz.ioc.val;

import java.util.Collection;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

public abstract class ListableValueProxy implements ValueProxy {

    protected Object obj;

    public ListableValueProxy(Object obj) {
        this.obj = obj;
    }

    protected abstract Object getValue(String key);

    public Object get(IocMaking ing) {
        if (obj == null)
            return null;
        if (obj.getClass().isArray() || obj instanceof Collection) {} else {
            obj = new Object[]{obj};
        }
        final StringBuilder sb = new StringBuilder();
        Lang.each(obj, new Each<Object>() {
            public void invoke(int index, Object ele, int length) throws ExitLoop, ContinueLoop, LoopException {
                String key = String.valueOf(ele);
                if (key.startsWith("!")) {
                    key = key.substring(1);
                    String dft = "";
                    if (key.contains(":")) {
                        dft = key.substring(key.indexOf(':') + 1);
                        key = key.substring(0, key.indexOf(':'));
                    }
                    Object val = getValue(key);
                    if (val != null) {
                        sb.append(val);
                    } else {
                        sb.append(dft);
                    }
                    return;
                }
                Object val = getValue(key);
                if (val == null) {
                    sb.append(key);
                } else {
                    sb.append(val);
                }
            }
        });
        return sb.toString();
    }
}
