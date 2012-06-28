package org.nutz.lang.util;

/**
 * 通用过滤器
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <T>
 */
public interface ObjFilter<T> {

    boolean accept(T o);

}
