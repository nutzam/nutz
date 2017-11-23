package org.nutz.mvc.testapp.classes.bean;

import org.nutz.mvc.annotation.Param;

public class Issue1277 {
    
    @Param(value="name",df="abc")
    public String name;
    
    @Param(value="age", df="123")
    public int age;
}
