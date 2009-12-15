package org.nutz.lang.segment;

import java.io.File;

import org.nutz.lang.Files;

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
}
