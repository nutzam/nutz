package org.nutz.lang.util;

import java.util.ArrayList;

import org.nutz.json.Json;
import org.nutz.lang.Lang;

public class LinkedLongArray {
    public LinkedLongArray() {
        this(256);
    }

    public LinkedLongArray(int size) {
        if (size < 0)
            Lang.makeThrow("width must >0!");
        this.width = size;
        cache = new ArrayList<long[]>();
    }

    private int offset;
    private int cursor;
    private int width;
    private ArrayList<long[]> cache;

    public LinkedLongArray push(long e) {
        long[] array;
        int row = cursor / width;
        int i = cursor % width;
        if (cache.size() == 0 || (cursor != offset && i == 0)) {
            array = new long[width];
            cache.add(array);
        } else {
            array = cache.get(row);
        }
        array[i] = e;
        cursor++;
        return this;
    }

    public long popFirst() {
        return innerGet(offset++);
    }

    public long popLast() {
        return innerGet(--cursor);
    }

    public LinkedLongArray popLast(long num) {
        for (long i = 0; i < num; i++)
            popLast();
        return this;
    }

    public long first() {
        if (size() == 0)
            return -1;
        return innerGet(offset);
    }

    public long last() {
        if (size() == 0)
            return -1;
        return innerGet(cursor - 1);
    }

    public LinkedLongArray set(int index, long e) {
        checkBound(index);
        index += offset;
        long[] array = cache.get(index / width);
        array[index % width] = e;
        return this;
    }

    private void checkBound(long index) {
        if (index >= size() || index < 0)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
    }

    public LinkedLongArray clear() {
        cache.clear();
        cursor = 0;
        offset = 0;
        return this;
    }

    private long innerGet(int index) {
        long[] array = cache.get(index / width);
        return array[index % width];
    }

    public long get(int index) {
        checkBound(index);
        return innerGet(index + offset);
    }

    public boolean isEmpty() {
        return 0 == cursor - offset;
    }

    public int size() {
        return cursor - offset;
    }

    public long[] toArray() {
        long[] re = new long[size()];
        for (int i = 0; i < re.length; i++)
            re[i] = this.get(i);
        return re;
    }

    public String toString() {
        return Lang.concat(',', toArray()).toString();
    }

    public String toJson() {
        return Json.toJson(toArray());
    }
}
