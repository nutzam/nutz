package org.nutz.lang.maplist;

/**
 * 将输入理解成Map+List
 * @author juqkai(juqkai@gmail.com)
 */
public interface MapListCompile<T> {
    public Object parse(T t);
}
