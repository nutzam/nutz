package org.nutz.lang.util;

public abstract class AbstractLifeCycle implements LifeCycle {

    @Override
    public void init() throws Exception{
        trigger(Event.INIT);
    }

    @Override
    public void fetch() throws Exception{
        trigger(Event.FETCH);
    }

    @Override
    public void depose() throws Exception{
        trigger(Event.DEPOSE);
    }
    
    public void trigger(Event event) throws Exception {}
}
