package org.nutz.mvc2;

import java.util.List;

import org.nutz.mvc.init.NutConfig;

/**
 * Action过滤器的提供工厂
 * @author wendal(wendal1985@gmail.com)
 *
 */
public interface ActionFilterFactory {

	List<ActionFilter> get(NutConfig config);
	
}
