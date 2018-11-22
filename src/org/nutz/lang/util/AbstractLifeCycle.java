package org.nutz.lang.util;

public abstract class AbstractLifeCycle implements LifeCycle {

    public void init() throws Exception{
        trigger(Event.INIT);
    }

    public void fetch() throws Exception{
        trigger(Event.FETCH);
    }

    public void depose() throws Exception{
        trigger(Event.DEPOSE);
    }
    
    public void trigger(Event event) throws Exception {}
}
