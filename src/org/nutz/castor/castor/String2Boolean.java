package org.nutz.castor.castor;

import java.util.regex.Pattern;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class String2Boolean extends Castor<String, Boolean> {

	private static Pattern PTN = Pattern.compile("f|false|0|f|off|no", Pattern.CASE_INSENSITIVE);

	@Override
	protected Boolean cast(String src, Class<?> toType, String... args) throws FailToCastObjectException {
		if (src.length() == 0)
			return false;
		return !PTN.matcher(src).find();
	}

}
