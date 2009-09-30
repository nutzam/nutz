package org.nutz.lang.util;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.nutz.json.Json;
import org.nutz.lang.Lang;

public class LinkedArray<T> {

	public LinkedArray() {
		this(256);
	}

	public LinkedArray(int size) {
		if (size < 0)
			Lang.makeThrow("width must >0!");
		this.width = size;
		cache = new ArrayList<T[]>();
	}

	private int offset;
	private int cursor;
	private int width;
	private ArrayList<T[]> cache;

	@SuppressWarnings("unchecked")
	public LinkedArray<T> push(T e) {
		T[] array;
		int row = cursor / width;
		int i = cursor % width;
		if (cache.size() == 0 || (cursor != offset && i == 0)) {
			array = (T[]) Array.newInstance(e.getClass(), width);
			cache.add(array);
		} else {
			array = cache.get(row);
		}
		array[i] = e;
		cursor++;
		return this;
	}

	public LinkedArray<T> pushAll(T... es) {
		for (T e : es)
			push(e);
		return this;
	}

	public T popFirst() {
		return innerGet(offset++);
	}

	public String popFirst(int num) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < num; i++)
			sb.append(popFirst());
		return sb.toString();
	}

	public T popLast() {
		return innerGet(--cursor);
	}

	public LinkedArray<T> popLast(int num) {
		for (int i = 0; i < num; i++)
			popLast();
		return this;
	}

	public T first() {
		if (size() == 0)
			return null;
		return innerGet(offset);
	}

	public T last() {
		if (size() == 0)
			return null;
		return innerGet(cursor - 1);
	}

	public LinkedArray<T> set(int index, T e) {
		checkBound(index);
		index += offset;
		T[] array = cache.get(index / width);
		array[index % width] = e;
		return this;
	}

	private void checkBound(int index) {
		if (index >= size() || index < 0)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
	}

	public LinkedArray<T> clear() {
		cache.clear();
		cursor = 0;
		offset = 0;
		return this;
	}

	private T innerGet(int index) {
		T[] array = cache.get(index / width);
		return array[index % width];
	}

	public T get(int index) {
		checkBound(index);
		return innerGet(index + offset);
	}

	public boolean isEmpty() {
		return 0 == cursor - offset;
	}

	public int size() {
		return cursor - offset;
	}

	@SuppressWarnings("unchecked")
	public T[] toArray() {
		if (size() == 0)
			return (T[]) new Object[0];
		T[] re = (T[]) Array.newInstance(first().getClass(), size());
		for (int i = 0; i < re.length; i++)
			re[i] = this.get(i);
		return re;
	}

	public String toString() {
		return Json.toJson(toArray());
	}

	public String popAll() {
		String re = toString();
		clear();
		return re;
	}

	public boolean contains(T obj) {
		for (int i = 0; i < size(); i++)
			if (get(i).equals(obj))
				return true;
		return false;
	}
}
