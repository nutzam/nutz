package org.nutz.mvc;


/**
 * 处理器的工厂类
 * <p>
 * 根据给定的 ProcessorConfig 对象，生成一个处理器链表的头节点
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ActionChainMaker {

	ActionChain eval(NutConfig config, ActionInfo chainInfo);

}
