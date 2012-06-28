package org.nutz.lang.inject;

/**
 * 抽象注入接口
 * <p>
 * 封装了通过 setter 以及 field 两种方式设置值的区别
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Injecting {

    /**
     * 通过反射，向对象某一字段设置一个值
     * 
     * @param obj
     *            被设值的对象
     * @param value
     *            值
     */
    void inject(Object obj, Object value);

}