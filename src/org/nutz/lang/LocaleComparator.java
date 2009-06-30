package org.nutz.lang;

import java.util.Comparator;
import java.util.Locale;

public class LocaleComparator implements Comparator<Locale> {
	private static final LocaleComparator lc = new LocaleComparator();

	public static LocaleComparator me() {
		return lc;
	}

	public int compare(Locale l1, Locale l2) {
		if (null == l1)
			if (null == l2)
				return 0;
			else
				return -1;
		if (null == l2)
			return 1;
		return l1.toString().compareTo(l2.toString());
	}

}
