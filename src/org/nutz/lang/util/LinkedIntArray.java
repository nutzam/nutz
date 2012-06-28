package org.nutz.lang.util;

import java.util.ArrayList;

import org.nutz.json.Json;
import org.nutz.lang.Lang;

public class LinkedIntArray {

    public LinkedIntArray() {
        this(256);
    }

    public LinkedIntArray(int size) {
        if (size <= 0)
            Lang.makeThrow("width must >0!");
        this.width = size;
        cache = new ArrayList<int[]>();
    }

    private int offset;
    private int cursor;
    private int width;
    private ArrayList<int[]> cache;

    public LinkedIntArray push(int e) {
        int[] array;
        int row = cursor / width;
        int i = cursor % width;
        if (cache.size() == 0 || (cursor != offset && i == 0)) {
            array = new int[width];
            cache.add(array);
        } else {
            array = cache.get(row);
        }
        array[i] = e;
        cursor++;
        return this;
    }

    public int popFirst() {
        return innerGet(offset++);
    }

    public int popLast() {
        return innerGet(--cursor);
    }

    public LinkedIntArray popLast(int num) {
        for (int i = 0; i < num; i++)
            popLast();
        return this;
    }

    public int first() {
        if (size() == 0)
            return (char) 0;
        return innerGet(offset);
    }

    public int last() {
        if (size() == 0)
            return (char) 0;
        return innerGet(cursor - 1);
    }

    public LinkedIntArray set(int index, int e) {
        checkBound(index);
        index += offset;
        int[] array = cache.get(index / width);
        array[index % width] = e;
        return this;
    }

    public LinkedIntArray setLast(int e) {
        set(this.size() - 1, e);
        return this;
    }

    private void checkBound(int index) {
        if (index >= size() || index < 0)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
    }

    public LinkedIntArray clear() {
        cache.clear();
        cursor = 0;
        offset = 0;
        return this;
    }

    private int innerGet(int index) {
        int[] array = cache.get(index / width);
        return array[index % width];
    }

    public int get(int index) {
        checkBound(index);
        return innerGet(index + offset);
    }

    public boolean isEmpty() {
        return 0 == cursor - offset;
    }

    public int size() {
        return cursor - offset;
    }

    public int[] toArray() {
        int[] re = new int[size()];
        for (int i = 0; i < re.length; i++)
            re[i] = (char) this.get(i);
        return re;
    }

    public String toString() {
        return Lang.concat(',', toArray()).toString();
    }

    public String toJson() {
        return Json.toJson(toArray());
    }
}
