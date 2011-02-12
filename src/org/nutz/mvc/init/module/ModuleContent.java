package org.nutz.mvc.init.module;

import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.mvc.ActionInvoker;
import org.nutz.mvc.ActionInvoking;
import org.nutz.mvc.init.NutConfig;
import org.nutz.mvc.init.PathInfo;
import org.nutz.mvc.init.PathNode;

/**
 * 模块控制器
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class ModuleContent {
	ModuleTree<?> mt;
	private PathNode<ActionInvoker> root;
	
	public ModuleContent(NutConfig config, Context context, Class<?> mainModule){
		this.root = new PathNode<ActionInvoker>();
		mt = new Main(context, mainModule, null, config.getIoc());
	}
	
	public void init() throws Throwable{
		mt.scan();
		mt.parse(root);
	}
	
	public ActionInvoking get(String path) {
		PathInfo<ActionInvoker> info = root.get(path);
		String[] args = Strings.splitIgnoreBlank(info.getRemain(), "[/]");
		return new ActionInvoking(info, args);
	}
}
