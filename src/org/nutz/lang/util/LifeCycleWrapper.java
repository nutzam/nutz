package org.nutz.lang.util;

import java.util.ArrayList;
import java.util.List;

public class LifeCycleWrapper extends AbstractLifeCycle {
    
    protected Object proxy;
    
    protected List<LifeCycle.Listener> listeners = new ArrayList<LifeCycle.Listener>();
    
    public LifeCycleWrapper(Object proxy) {
        this.proxy = proxy;
    }
    
    public void trigger(Event event) throws Exception {
        for (Listener listener : listeners)
            if(!listener.trigger(proxy, event))
                return;
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }
    
    public void clearListeners() {
        this.listeners.clear();
    }
}
