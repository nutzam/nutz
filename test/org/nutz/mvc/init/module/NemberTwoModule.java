package org.nutz.mvc.init.module;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@At("/two")
public class NemberTwoModule {

	@At
	public void check() {}

	@At("/say")
	@Ok("json")
	public String say() {
		return "haha";
	}
	
	@At
	@Ok("json")
	@Fail("json")
	public boolean login(@Param("username") String userName, 
			             @Param("password") String password,
			             @Param("authCode") Long authCode){
		return !(userName == null || password == null);
	}
}
