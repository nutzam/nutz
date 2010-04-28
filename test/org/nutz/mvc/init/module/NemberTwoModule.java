package org.nutz.mvc.init.module;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@At("/two")
public class NemberTwoModule {

	@At
	public void check() {}

	@At("/say")
	@Ok("json")
	public String say() {
		return "haha";
	}
}
