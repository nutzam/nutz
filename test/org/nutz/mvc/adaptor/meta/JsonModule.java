package org.nutz.mvc.adaptor.meta;

import java.io.UnsupportedEncodingException;

import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@AdaptBy(type=JsonAdaptor.class)
@At("/json")
@Ok("json")
@Fail("json")
public class JsonModule {
	
	@At
	public String hello(@Param("pet") Pet pet) throws UnsupportedEncodingException{
		return "!!"+pet.getName()+"!!";
	}

}
