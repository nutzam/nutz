package com.zzh.segment;

import java.io.InputStream;
import java.util.List;

public interface Segment {

	public Segment set(String key, boolean v);

	public Segment set(String key, int v);

	public Segment set(String key, double v);

	public Segment set(String key, float v);

	public Segment set(String key, long v);

	public Segment set(String key, byte v);

	public Segment set(String key, short v);

	public Segment set(String key, Object v);

	public Segment add(String key, Object v);

	public void clearAll();

	public Segment born();

	public Segment clone();

	public boolean contains(String key);

	public List<String> keys();

	public List<Object> values();

	public int getIndex(String key);

	public Segment valueOf(String str);

	public void parse(InputStream ins);

	public CharSequence render();

}
