package com.zzh.dao;

import java.util.regex.Pattern;

import com.zzh.lang.Strings;

public class FieldMatcher {

	public static FieldMatcher make(String actived, String locked) {
		FieldMatcher fm = new FieldMatcher();
		if (!Strings.isBlank(actived))
			fm.actived = Pattern.compile(actived);
		if (!Strings.isBlank(locked))
			fm.locked = Pattern.compile(locked);
		return fm;
	}

	private Pattern actived;
	private Pattern locked;

	void setActived(Pattern actived) {
		this.actived = actived;
	}

	void setLocked(Pattern locked) {
		this.locked = locked;
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
