package org.nutz.lang.segment;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

public interface Segment {

	Segment setAll(Object v);

	Segment set(String key, boolean v);

	Segment set(String key, int v);

	Segment set(String key, double v);

	Segment set(String key, float v);

	Segment set(String key, long v);

	Segment set(String key, byte v);

	Segment set(String key, short v);

	Segment set(String key, Object v);

	Segment add(String key, Object v);

	void clearAll();

	Segment born();

	Segment clone();

	boolean contains(String key);

	Set<String> keys();

	List<Object> values();

	List<Integer> getIndex(String key);

	Segment valueOf(String str);

	void parse(InputStream ins);

	CharSequence render();

	String getOrginalString();

}
