package org.nutz.maplist;

/**
 * 将输入理解成Map+List
 * @author juqkai(juqkai@gmail.com)
 */
public interface MaplistCompile<T> {
    public Object parse(T t);
}
