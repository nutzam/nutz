package org.nutz.mvc.testapp.classes.action.mapping;

import org.nutz.mvc.annotation.ApiVersion;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@Ok("raw")
@At("/mapping/issue1530/{version}")
@ApiVersion("v1")
public class Issue1530MappingAction {

    @ApiVersion("v1")
    @At("/yourname")
    public String getYourName() {
        return "v1";
    }
    
    @ApiVersion("v2")
    @At("/yourname")
    public String getYourName2() {
        return "v2";
    }
}