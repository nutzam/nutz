package org.nutz.mvc.init.conf;

import org.nutz.json.Json;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

public class MainModuleA {

	@At("/param/a")
	@Ok("raw")
	public String paramIsArray(@Param("ids") long[] ids) {
		return Json.toJson(ids);
	}

}
