package org.nutz.lang.eject;

/**
 * 抽象取值接口
 * <p>
 * 封装了通过 getter 以及 field 两种方式获取值的区别
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Ejecting {

    /**
     * 通过反射，从一个对象中获取某一字段的值
     * 
     * @param obj
     *            被取值的对象
     * @return 值
     */
    Object eject(Object obj);

}
