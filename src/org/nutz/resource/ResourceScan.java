package org.nutz.resource;

import java.util.List;

/**
 * 资源扫描,实现类需要支持被多次重复调用
 * 
 * @author Wendal(wendal1985@gmail.com)
 * 
 */
public interface ResourceScan {

	List<NutResource> list(String src, String filter);

}
