package org.nutz.mvc.view;

import org.nutz.ioc.Ioc;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

public class BuiltinViewMaker implements ViewMaker {

	public View make(Ioc ioc, String type, String value) {
		if (type.equals("jsp")) {
			return new NamePathJspView(value);
		} else if (type.equals("json")) {
			if (Strings.isBlank(value)) {
				return new UTF8JsonView(JsonFormat.compact());
			} else {
				JsonFormat format = Json.fromJson(JsonFormat.class, value);
				return new UTF8JsonView(format);
			}
		} else if (type.equals("redirect")) {
			return new ServerRedirectView(value);
		} else if (type.equals("void")) {
			return new VoidView();
		} else if (type.equals("ioc")) {
			return ioc.get(View.class, value);
		} else if (type.equals("http")) {
			int sc = Integer.parseInt(value);
			return new HttpStatusView(sc);
		}
		return null;
	}

}
