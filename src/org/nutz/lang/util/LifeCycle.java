package org.nutz.lang.util;

import java.util.EventListener;

public interface LifeCycle {
    
    public enum Event {
        INIT, FETCH, DEPOSE
    }

    void init() throws Exception;
    
    void fetch() throws Exception;
    
    void depose() throws Exception;
    
    public interface Listener extends EventListener {
        boolean trigger(Object obj, Event state) throws Exception;
    }
}
