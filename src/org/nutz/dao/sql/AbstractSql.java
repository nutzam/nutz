package org.nutz.dao.sql;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.nutz.dao.Condition;
import org.nutz.lang.segment.Segment;

public abstract class AbstractSql implements Sql {

	private Segment seg;

	public Segment add(String key, Object v) {
		return seg.add(key, v);
	}

	public void clearAll() {
		seg.clearAll();
	}

	public boolean contains(String key) {
		return seg.contains(key);
	}

	public List<Integer> getIndex(String key) {
		return seg.getIndex(key);
	}

	public Set<String> keys() {
		return seg.keys();
	}

	public void parse(InputStream ins) {
		seg.parse(ins);
	}

	public CharSequence render() {
		return seg.render();
	}

	public Segment set(String key, boolean v) {
		return seg.set(key, v);
	}

	public Segment set(String key, byte v) {
		return seg.set(key, v);
	}

	public Segment set(String key, double v) {
		return seg.set(key, v);
	}

	public Segment set(String key, float v) {
		return seg.set(key, v);
	}

	public Segment set(String key, int v) {
		return seg.set(key, v);
	}

	public Segment set(String key, long v) {
		return seg.set(key, v);
	}

	public Segment set(String key, Object v) {
		return seg.set(key, v);
	}

	public Segment set(String key, short v) {
		return seg.set(key, v);
	}

	public Segment setAll(Object v) {
		return seg.setAll(v);
	}

	public String toOrginalString() {
		return seg.toOrginalString();
	}

	public Segment valueOf(String str) {
		return seg.valueOf(str);
	}

	public List<Object> values() {
		return seg.values();
	}

	private SqlContext context;
	private SqlCallback callback;
	private Condition condition;
	private Object result;

	public SqlContext getContext() {
		return context;
	}

	public Sql setContext(SqlContext context) {
		this.context = context;
		return this;
	}

	public SqlCallback getCallback() {
		return callback;
	}

	public Sql setCallback(SqlCallback callback) {
		this.callback = callback;
		return this;
	}

	public Condition getCondition() {
		return condition;
	}

	public Sql setCondition(Condition condition) {
		this.condition = condition;
		return this;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Sql clone() {
		Sql sql = this.born();
		return sql.setContext(context).setCallback(callback).setCondition(condition);
	}
}
