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
	 * @param name
	 * @return
	 */
	public String[] extractor(String name);
	/**
	 * 键
	 * @return
	 */
	public Set<String> keys();
}
