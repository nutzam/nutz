package org.nutz.mvc.impl.chainconfig;

import java.util.List;

/**
 * NutActionChainMaker内部使用的接口,用于读取配置文件
 * @author wendal(wendal1985@gmail.com)
 */
public interface ActionChainMakerConfiguration {

    public List<String> getProcessors(String key);
    
    public String getErrorProcessor(String key);
}
