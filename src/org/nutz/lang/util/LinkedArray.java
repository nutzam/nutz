package org.nutz.lang.util;

import java.util.ArrayList;

import org.nutz.json.Json;
import org.nutz.lang.Lang;

public class LinkedArray<T> extends ArrayList<T> {

    public LinkedArray() {
    }

    public LinkedArray(int size) {
        super(size);
    }

    public T last() {
        return get(size() - 1);
    }
    
    public void push(T t) {
        this.add(t);
    }

    public String toString() {
        return Lang.concat(',', toArray()).toString();
    }

    public String toJson() {
        return Json.toJson(toArray());
    }
}
