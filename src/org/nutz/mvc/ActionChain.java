package org.nutz.mvc;

/**
 * Mvc 处理器接口
 * <p>
 * 这个接口的实现必须保证线程安全，即，不能在自己存储私有属性
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public interface ActionChain {

    void doChain(ActionContext ac);

}
