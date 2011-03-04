package org.nutz.mvc.impl.processor;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.Segments;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.ObjectInfo;
import org.nutz.mvc.Processor;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.impl.Loadings;
import org.nutz.mvc.view.VoidView;

public abstract class AbstractProcessor implements Processor {

	private Processor next;
	
	public void init(NutConfig config, ActionInfo ai) throws Throwable {
	}

	public void setNext(Processor next) {
		this.next = next;
	}

	protected void doNext(ActionContext ac) throws Throwable {
		if (null != next)
			next.process(ac);
	}

	protected static <T> T evalObj(NutConfig config, ObjectInfo<T> info) {
		return null == info ? null : Loadings.evalObj(config, info.getType(), info.getArgs());
	}

	protected static View evalView(NutConfig config, ActionInfo ai, String viewType) {
		if (Strings.isBlank(viewType))
			return new VoidView();

		String str = Segments.replace(viewType, config.getLoadingContext());
		int pos = str.indexOf(':');
		String type, value;
		if (pos > 0) {
			type = Strings.trim(str.substring(0, pos).toLowerCase());
			value = Strings.trim(pos >= (str.length() - 1) ? null : str.substring(pos + 1));
		} else {
			type = str;
			value = null;
		}
		for (ViewMaker maker : ai.getViewMakers()) {
			View view = maker.make(config.getIoc(), type, value);
			if (null != view)
				return view;
		}
		throw Lang.makeThrow("Can not eval %s(\"%s\") View for %s", viewType, str, ai.getMethod());
	}
}
