package org.nutz.ioc.aop.config.imlp;

import java.util.List;

/**
 * 
 * 根据Json配置文件判断需要拦截哪些方法
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class JsonAopConfigration extends AbstractAopConfigration {

	@Override
	public void setAopItemList(List<AopConfigrationItem> aopItemList) {
		super.setAopItemList(aopItemList);
	}

}
