package org.nutz.mvc.testapp.classes.action.mapping;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;

@IocBean
@At("/mapping/issue1337")
public class Issue1337StudentAction extends Issue1212BaseAction {

    @Override
    public void sayhi() {
        super.sayhi();
    }
    
}
