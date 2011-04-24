package org.nutz.json2;

import java.util.List;
import java.util.Map;

public class AllType {
	private String jstring;
	private int jint;
	private long jlong;
	private float jfloat;
	private double jdouble;
	private boolean jboolean;
	private AllType jnull;
	private List<Integer> jlist;
	private AllType[] jarray;
	private Map<String, Integer> jmap;
	public String getJstring() {
		return jstring;
	}
	public void setJstring(String jstring) {
		this.jstring = jstring;
	}
	public int getJint() {
		return jint;
	}
	public void setJint(int jint) {
		this.jint = jint;
	}
	public long getJlong() {
		return jlong;
	}
	public void setJlong(long jlong) {
		this.jlong = jlong;
	}
	public double getJdouble() {
		return jdouble;
	}
	public void setJdouble(double jdouble) {
		this.jdouble = jdouble;
	}
	public boolean isJboolean() {
		return jboolean;
	}
	public void setJboolean(boolean jboolean) {
		this.jboolean = jboolean;
	}
	public List<Integer> getJlist() {
		return jlist;
	}
	public void setJlist(List<Integer> jlist) {
		this.jlist = jlist;
	}
	public Map<String, Integer> getJmap() {
		return jmap;
	}
	public void setJmap(Map<String, Integer> jmap) {
		this.jmap = jmap;
	}
	public AllType[] getJarray() {
		return jarray;
	}
	public void setJarray(AllType[] jarray) {
		this.jarray = jarray;
	}
	public AllType getJnull() {
		return jnull;
	}
	public void setJnull(AllType jnull) {
		this.jnull = jnull;
	}
	public float getJfloat() {
		return jfloat;
	}
	public void setJfloat(float jfloat) {
		this.jfloat = jfloat;
	}
}
