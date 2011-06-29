package org.nutz.el2.opt;

import java.util.List;

public interface RunMethod {

	/**
	 * 执行方法
	 * @param fetchParam 参数
	 * @return
	 */
	Object run(List<Object> fetchParam);

}
