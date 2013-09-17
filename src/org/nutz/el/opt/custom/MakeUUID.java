package org.nutz.el.opt.custom;

import java.util.List;
import java.util.UUID;

import org.nutz.el.opt.RunMethod;
import org.nutz.plugin.Plugin;

public class MakeUUID implements RunMethod, Plugin {

	public boolean canWork() {
		return true;
	}

	public Object run(List<Object> fetchParam) {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public String fetchSelf() {
		return "uuid";
	}

}
