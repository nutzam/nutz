package org.nutz.mvc;

/**
 * 处理器的工厂类
 * <p>
 * 根据给定的 ProcessorConfig 对象，生成一个处理器链表的头节点
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ActionChainMaker {

    /**
     * 根据配置信息，生成一个动作链
     * 
     * @param config
     *            应用配置信息对象
     * @param ai
     *            入口函数配置信息
     * @return 动作链
     */
    ActionChain eval(NutConfig config, ActionInfo ai);

}
