package org.nutz.lang.segment;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * 字符串片段。你可以通过这个接口的函数，为片段中的占位符设值。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Segment {

	Segment setAll(Object v);

	/**
	 * 根据对象设置每个插入点的值。
	 * 
	 * @param obj
	 *            可以是 POJO 或者 Map
	 * @return Segment
	 */
	Segment setBy(Object obj);

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
