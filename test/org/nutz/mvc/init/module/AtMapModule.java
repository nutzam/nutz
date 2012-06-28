package org.nutz.mvc.init.module;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

@At("/atmap")
@Ok("json")
@Fail("json")
public class AtMapModule {

    @At(key = "at.abc", value = "/ABC")
    public String abc() {
        return ">>abc";
    }

    @At(key = "at.xyz")
    public String xyz() {
        return ">>xyz";
    }

}
