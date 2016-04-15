package org.nutz.ioc;

import java.util.ArrayList;
import java.util.List;

public class IocException extends RuntimeException {

    private static final long serialVersionUID = -420118435729449317L;
    
    public List<String> beanNames = new ArrayList<String>();

    public IocException(String beanName, String format, Object ...args) {
        super(String.format(format, args));
        beanNames.add(beanName);
    }
    
    public IocException(String beanName, Throwable cause, String format, Object ...args) {
        super(String.format(format, args), cause);
        beanNames.add(beanName);
    }
    
    public void addBeanNames(String beanName) {
        if (!beanNames.contains(beanName))
            beanNames.add(0, beanName);
    }
    
    public String getMessage() {
        String msg = super.getMessage();
        if (msg.length() > 4096)
            return msg;
        return beanNames+" # " + msg;
    }
}
