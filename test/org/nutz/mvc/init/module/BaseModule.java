package org.nutz.mvc.init.module;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@InjectName
@At("/base")
@Ok("json")
public class BaseModule {
    
    private String nameX;

    @At
    public boolean login(){
        return getNameX() != null;
    }
    
    public void setNameX(String nameX) {
        this.nameX = nameX;
    }
    
    public String getNameX() {
        return nameX;
    }
}
