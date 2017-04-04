package org.nutz.lang.reflect;

public interface FastMethod {

    Object invoke(Object obj, Object ... args) throws Exception;
    
}
