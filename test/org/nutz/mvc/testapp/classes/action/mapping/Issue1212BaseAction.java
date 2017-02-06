package org.nutz.mvc.testapp.classes.action.mapping;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

public abstract class Issue1212BaseAction {

    @At
    @Ok("http:200")
    public void sayhi(){}
}
