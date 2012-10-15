package org.nutz.mvc.impl;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.Processor;

public class NutActionChain implements ActionChain {

    private Processor head;

    private Processor errorProcessor;
    
    private Method method;

    public NutActionChain(List<Processor> list, Processor errorProcessor, Method method) {
        if (null != list) {
            Iterator<Processor> it = list.iterator();
            if (it.hasNext()) {
                head = it.next();
                Processor p = head;
                while (it.hasNext()) {
                    Processor next = it.next();
                    p.setNext(next);
                    p = next;
                }
            }
        }
        this.errorProcessor = errorProcessor;
        this.method = method;
    }

    public void doChain(ActionContext ac) {
        if (null != head)
            try {
                head.process(ac);
            }
            catch (Throwable e) {
                ac.setError(e);
                try {
                    errorProcessor.process(ac);
                }
                catch (Throwable ee) {
                    throw Lang.wrapThrow(ee);
                }
        }
    }

    
    String methodStr;
    public String toString() {
        if (methodStr == null)
            methodStr = Lang.simpleMetodDesc(method);
        return methodStr;
    }
}
