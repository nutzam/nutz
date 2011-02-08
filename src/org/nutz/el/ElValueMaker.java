package org.nutz.el;

/**
 * 根据传入的字符串，生成一个ElItem 的实例
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ElValueMaker {

	ElValue make(Object obj);

}
