package org.nutz.lang.born;

/**
 * 对象抽象创建方式
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @param <T>
 */
public interface Borning<T> {

    T born(Object[] args);

}
