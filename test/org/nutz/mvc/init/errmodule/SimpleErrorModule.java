package org.nutz.mvc.init.errmodule;

import org.nutz.mvc.annotation.At;

public class SimpleErrorModule {

    @At({"/check", ""})
    public void check() {}

}
