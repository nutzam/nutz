package org.nutz.mvc.i18n;

import java.util.Set;

import org.nutz.mvc.impl.NutMessageMap;

public interface LocalizationManager {

    void setDefaultLocal(String local);
    
    String getDefaultLocal();
    
    Set<String> getLocals();
    
    NutMessageMap getMessageMap(String local);
    
    String getMessage(String local, String key);
}
