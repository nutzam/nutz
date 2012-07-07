package org.nutz.mvc.impl;

import java.lang.reflect.Method;

import org.nutz.mvc.adaptor.AbstractAdaptor;
import org.nutz.mvc.adaptor.ParamInjector;


public class AdaptorErrorContext {

    public AdaptorErrorContext(int size) {
        errors = new Throwable[size];
    }
    
    protected Throwable[] errors;
    
    public Throwable[] getErrors() {
        return errors;
    }
    
    public void setError(int index, Throwable err, Method method, Object value, ParamInjector inj) {
        errors[index] = err;
    }
    
    protected Throwable adaptorErr;
    
    public void setAdaptorError(Throwable err, AbstractAdaptor adaptor) {
        this.adaptorErr = err;
    }
    
    public Throwable getAdaptorErr() {
        return adaptorErr;
    }
}
