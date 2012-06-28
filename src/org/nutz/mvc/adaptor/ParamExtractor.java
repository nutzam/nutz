package org.nutz.mvc.adaptor;

import java.util.Set;

/**
 * 参数提取器
 * @author juqkai(juqkai@gmail.com)
 *
 */
public interface ParamExtractor {
    /**
     * 根据名称提取值
     */
    public String[] extractor(String name);
    /**
     * 键
     */
    public Set<String> keys();
}
