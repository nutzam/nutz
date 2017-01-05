package org.nutz.lang.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nutz.json.Json;
import org.nutz.lang.Lang;

public class LinkedArray<T> {

    public LinkedArray() {
        this(256);
    }

    public LinkedArray(int size) {
        this(null, size);
    }

    public LinkedArray(Class<T> eleType, int size) {
        this.eleType = eleType;
        if (size <= 0)
            Lang.makeThrow("width must >0!");
        this.width = size;
        cache = new ArrayList<T[]>();
    }

    private Class<T> eleType;
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
            if (null == eleType)
                array = (T[]) Array.newInstance(e.getClass(), width);
            else
                array = (T[]) Array.newInstance(eleType, width);
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
        if (size() == 0) {
            if (null == eleType)
                return (T[]) new Object[0];
            return (T[]) Array.newInstance(eleType, 0);
        }
        T[] re;
        if (null == eleType) {
            re = (T[]) Array.newInstance(first().getClass(), size());
        } else {
            re = (T[]) Array.newInstance(eleType, size());
        }
        for (int i = 0; i < re.length; i++)
            re[i] = this.innerGet(i);
        return re;
    }

    public List<T> toList() {
        int len = size();
        ArrayList<T> list = new ArrayList<T>(len);
        for (int i = 0; i < len; i++)
            list.add(innerGet(i));
        return list;
    }

    class LinkedArrayIterator<E> implements Iterator<E> {

        LinkedArray<E> stack;
        int i;

        LinkedArrayIterator(LinkedArray<E> stack) {
            this.stack = stack;
            i = stack.offset;
        }

        public boolean hasNext() {
            return i < stack.cursor;
        }

        public E next() {
            if (i >= stack.offset && i < stack.cursor)
                return stack.innerGet(i++);
            return null;
        }

        public void remove() {
            throw Lang.noImplement();
        }

    }

    public Iterator<T> iterator() {
        return new LinkedArrayIterator<T>(this);
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
            if (innerGet(i).equals(obj))
                return true;
        return false;
    }

    public int indexOf(T obj) {
        for (int i = 0; i < size(); i++)
            if (innerGet(i).equals(obj))
                return i;
        return -1;
    }

    public int lastIndexOf(T obj) {
        for (int i = size() - 1; i >= 0; i--)
            if (innerGet(i).equals(obj))
                return i;
        return -1;
    }
}
