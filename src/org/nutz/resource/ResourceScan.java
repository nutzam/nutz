package org.nutz.resource;

import java.util.List;

/**
 * 资源扫描,实现类需要支持被多次重复调用
 * 
 * @author Wendal(wendal1985@gmail.com)
 * 
 */
public interface ResourceScan {

	/**
	 * 传入的src必须为路径,如果不是/结尾,则自动补齐,基于文件的扫描,由Scans获取路径后在调用这个接口进行扫描
	 * @param src
	 * @param filter
	 * @return
	 */
	List<NutResource> list(String src, String filter);

}
