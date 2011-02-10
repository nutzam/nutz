package org.nutz.mvc2;

import java.util.List;

import org.nutz.mvc.init.NutConfig;

public interface ActionFilterFactory {

	List<ActionFilter> get(NutConfig config);
	
}
