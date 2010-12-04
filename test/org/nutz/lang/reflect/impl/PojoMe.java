package org.nutz.lang.reflect.impl;

import org.nutz.dao.DB;
import org.nutz.mvc.annotation.At;

public class PojoMe {
	
	public static int count;
	
	//*****************
	int a;
	short b;
	long c;
	byte d;
	char e;
	double f;
	float g;
	boolean h;
	Object obj;
	DB db;
	At at;
	Object[] array;
	//*****************

	public PojoMe() {
		count++;
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public short getB() {
		return b;
	}

	public void setB(short b) {
		this.b = b;
	}

	public long getC() {
		return c;
	}

	public void setC(long c) {
		this.c = c;
	}

	public byte getD() {
		return d;
	}

	public void setD(byte d) {
		this.d = d;
	}

	public char getE() {
		return e;
	}

	public void setE(char e) {
		this.e = e;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public float getG() {
		return g;
	}

	public void setG(float g) {
		this.g = g;
	}

	public boolean isH() {
		return h;
	}

	public void setH(boolean h) {
		this.h = h;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public DB getDb() {
		return db;
	}

	public void setDb(DB db) {
		this.db = db;
	}

	public At getAt() {
		return at;
	}

	public void setAt(At at) {
		this.at = at;
	}
	
	public Object[] getArray() {
		return array;
	}
	
	public void setArray(Object[] array) {
		this.array = array;
	}
}
