package org.nutz.lang.random;

public class ArrayRandom<T> implements Random<T> {

	private T[] array;
	private int len;
	private java.util.Random r = new java.util.Random();

	public ArrayRandom(T[] array) {
		this.array = array;
		len = array.length;
	}

	public T next() {
		if (len <= 0)
			return null;
		if (len == 1)
			return array[--len];
		int index = r.nextInt(len);
		if (index == len - 1)
			return array[--len];
		T c = array[index];
		array[index] = array[--len];
		return c;
	}

}
