package org.nutz.lang.segment;

import java.io.File;

import org.nutz.lang.Files;
import org.nutz.lang.util.Context;

public class Segments {

	public static Segment fill(Segment seg, Object obj) {
		if (null == obj || null == seg)
			return seg;
		return seg.setBy(obj);
	}

	public static Segment read(File f) {
		String txt = Files.read(f);
		return new CharSegment(txt);
	}

	public static String replace(Segment seg, Context context) {
		if (null == seg)
			return null;
		if (null == context)
			return seg.render().toString();

		for (String key : seg.keys()) {
			Object v = context.get(key);
			seg.set(key, v);
		}
		return seg.render().toString();
	}

	public static String replace(String pattern, Context context) {
		if (null == context)
			return pattern;
		return replace(new CharSegment(pattern), context);
	}

	public static Segment create(String str) {
		return new CharSegment(str);
	}
}
