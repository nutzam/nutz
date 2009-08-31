package org.nutz.dao;

import java.util.regex.Pattern;

import org.nutz.lang.Strings;

public class FieldMatcher {

	public static FieldMatcher make(String actived, String locked, boolean ignoreNull) {
		FieldMatcher fm = new FieldMatcher();
		fm.ignoreNull = ignoreNull;
		if (!Strings.isBlank(actived))
			fm.actived = Pattern.compile(actived);
		if (!Strings.isBlank(locked))
			fm.locked = Pattern.compile(locked);
		return fm;
	}

	private Pattern actived;
	private Pattern locked;
	private boolean ignoreNull;

	public boolean isIgnoreNull() {
		return ignoreNull;
	}

	public boolean match(String str) {
		if (null != locked)
			if (locked.matcher(str).find())
				return false;
		if (null != actived)
			if (!actived.matcher(str).find())
				return false;
		return true;
	}

}
