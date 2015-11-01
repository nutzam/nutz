package org.nutz.lang.util;

/**
 * 一个闭合器通用接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Closer<T> {

    T invoke();

}
