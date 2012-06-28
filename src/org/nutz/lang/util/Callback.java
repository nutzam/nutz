package org.nutz.lang.util;

/**
 * 带一个参数的通用回调接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <T>
 */
public interface Callback<T> {

    void invoke(T obj);

}
