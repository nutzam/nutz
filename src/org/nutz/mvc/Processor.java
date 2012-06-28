package org.nutz.mvc;


public interface Processor {
    
    void init(NutConfig config, ActionInfo ai) throws Throwable;

    void process(ActionContext ac) throws Throwable;
    
    void setNext(Processor p);

    Processor getNext();
}
