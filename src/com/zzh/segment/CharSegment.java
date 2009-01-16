package com.zzh.segment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.stream.CharInputStream;

public class CharSegment implements Segment {

	public CharSegment() {
	}

	public CharSegment(String str) {
		valueOf(str);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Segment add(String key, Object v) {
		List<Integer> indexes = pps.get(key);
		if (null != indexes) {
			for (Iterator<Integer> it = indexes.iterator(); it.hasNext();) {
				int index = it.next().intValue();
				Object vs = values.get(index);
				if (vs instanceof List)
					((List<Object>) vs).add(v);
				else {
					List<Object> vl = new LinkedList<Object>();
					vl.add(v);
					values.set(index, vl);
				}
			}
		}
		return this;
	}

	@Override
	public void clearAll() {
		for (Iterator<List<Integer>> i = pps.values().iterator(); i.hasNext();)
			for (Iterator<Integer> ii = i.next().iterator(); ii.hasNext();)
				values.set(ii.next().intValue(), null);
	}

	@Override
	public boolean contains(String key) {
		return pps.containsKey(key);
	}

	@Override
	public Segment born() {
		CharSegment cs;
		try {
			cs = Mirror.me(this.getClass()).born();
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		cs.keys = new ArrayList<String>(keys.size());
		cs.keys.addAll(keys);
		cs.pps = new HashMap<String, List<Integer>>();
		cs.pps.putAll(pps);
		cs.values = new ArrayList<Object>(values.size());
		cs.values.addAll(values);
		cs.clearAll();
		return cs;
	}

	@Override
	public Segment clone() {
		// TODO Still need consider more cases...
		CharSegment cs;
		try {
			cs = Mirror.me(this.getClass()).born();
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		cs.keys = new ArrayList<String>(keys.size());
		cs.keys.addAll(keys);
		cs.pps = new HashMap<String, List<Integer>>();
		cs.pps.putAll(pps);
		cs.values = new ArrayList<Object>(values.size());
		for (int i = 0; i < values.size(); i++) {
			Object v = values.get(i);
			if (null == v)
				cs.values.add(null);
			else if (v instanceof CharSequence)
				cs.values.add(new String(new StringBuilder((CharSequence) v)));
			else {
				cs = Mirror.me(CharSegment.class).duplicate(this);
			}

		}
		return cs;
	}

	@Override
	public int getIndex(String key) {
		return keys.indexOf(key);
	}

	@Override
	public List<String> keys() {
		return this.keys;
	}

	@Override
	public List<Object> values() {
		return this.values;
	}

	private static Segment setValue(CharSegment seg, String key, Object v) {
		List<Integer> indexes = seg.pps.get(key);
		if (null != indexes)
			for (Iterator<Integer> it = indexes.iterator(); it.hasNext();)
				seg.values.set(it.next().intValue(), v);
		return seg;
	}

	@Override
	public Segment set(String key, boolean v) {
		return setValue(this, key, v);
	}

	@Override
	public Segment set(String key, int v) {
		return setValue(this, key, v);
	}

	@Override
	public Segment set(String key, double v) {
		return setValue(this, key, v);
	}

	@Override
	public Segment set(String key, float v) {
		return setValue(this, key, v);
	}

	@Override
	public Segment set(String key, long v) {
		return setValue(this, key, v);
	}

	@Override
	public Segment set(String key, byte v) {
		return setValue(this, key, v);
	}

	@Override
	public Segment set(String key, short v) {
		return setValue(this, key, v);
	}

	@Override
	public Segment set(String key, Object v) {
		return setValue(this, key, v);
	}

	private Map<String, List<Integer>> pps;
	private List<Object> values;
	private List<String> keys;

	@Override
	public void parse(InputStream ins) {
		pps = new HashMap<String, List<Integer>>();
		values = new ArrayList<Object>();
		keys = new ArrayList<String>();
		try {
			int c;
			StringBuffer sb = new StringBuffer();
			boolean isInPP = false;
			boolean lastIsString = false;
			while ((c = ins.read()) != -1) {
				if (isInPP && c == '}') { // In PlugPoint, and find }
					String key = sb.toString();
					values.add(null);
					lastIsString = false;
					List<Integer> indexes = pps.get(key);
					if (null == indexes) {
						indexes = new ArrayList<Integer>();
						pps.put(key, indexes);
						keys.add(key);
					}
					indexes.add(values.size() - 1);
					isInPP = false;
					sb = new StringBuffer();
				} else if (c == '$') { // Out of PlugPoint, and find $
					int cc = ins.read();
					switch (cc) {
					case '$':
						sb.append((char) c);
						break;
					case '{':
						if (sb.length() > 0) {
							values.add(sb);
							sb = new StringBuffer();
							lastIsString = true;
						}
						isInPP = true;
						break;
					case -1:
						break;
					default:
						sb.append((char) c).append((char) cc);
					}
				} else { // Normal char, just append it
					sb.append((char) c);
				}
			}
			if (sb.length() > 0) {
				if (isInPP) {
					sb.insert(0, "${");
					if ((pps.size() == 0 && values.size() > 0) || lastIsString)
						((StringBuffer) values.get(0)).append(sb);
					else
						values.add(sb);
				} else
					values.add(sb);
			}
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	@Override
	public Segment valueOf(String str) {
		parse(new CharInputStream(str));
		return this;
	}

	@Override
	public CharSequence render() {
		StringBuffer sb = new StringBuffer();
		for (Iterator<?> it = values.iterator(); it.hasNext();) {
			Object o = it.next();
			if (o == null)
				continue;
			if (o instanceof List) {
				for (Iterator<?> ii = ((List<?>) o).iterator(); ii.hasNext();) {
					sb.append(ii.next());
				}
			} else
				sb.append(o);
		}
		return sb;
	}

	@Override
	public String toString() {
		return render().toString();
	}

}
