package org.nutz.mvc.testapp.classes.action.adaptor;

import java.util.Date;

import org.nutz.mvc.annotation.Param;

public class Issue543 {

    @Param(value = "d", dfmt = "yyyyMMdd")
    public Date d;

}
