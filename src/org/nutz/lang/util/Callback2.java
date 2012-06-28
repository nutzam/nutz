package org.nutz.lang.util;

/**
 * 带两个参数的通用回调接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <T0>
 * @param <T1>
 */
public interface Callback2<T0, T1> {

    void invoke(T0 arg0, T1 arg1);

}
