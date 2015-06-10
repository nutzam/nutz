package org.nutz.el.opt.custom;

import java.util.List;
import java.util.UUID;

import org.nutz.el.opt.RunMethod;
import org.nutz.lang.random.R;
import org.nutz.plugin.Plugin;

public class MakeUUID implements RunMethod, Plugin {

	public boolean canWork() {
		return true;
	}

	public Object run(List<Object> fetchParam) {
	    if (fetchParam.isEmpty() || !(fetchParam.get(0) instanceof Number))
	        return UUID.randomUUID().toString().replace("-", "");
	    int type = ((Number)fetchParam.get(0)).intValue();
	    switch (type) {
        case 32:
            return R.UU32();
        case 64:
            return R.UU64();
        default:
            return UUID.randomUUID().toString().replace("-", "");
        }
	}

	public String fetchSelf() {
		return "uuid";
	}

}
