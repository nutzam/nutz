package org.nutz.mvc.impl;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.UrlMapping;
import org.nutz.mvc.annotation.BlankAtException;

public class UrlMappingImpl implements UrlMapping {

	private static final Log log = Logs.getLog(UrlMappingImpl.class);

	private MappingNode<ActionChain> root;

	public UrlMappingImpl() {
		this.root = new MappingNode<ActionChain>();
	}

	public void add(ActionChainMaker maker, ActionInfo ai, NutConfig config) {
		ActionChain chain = maker.eval(config, ai);
		for (String path : ai.getPaths()) {
			if (Strings.isBlank(path))
				throw new BlankAtException(ai.getModuleType(), ai.getMethod());
			root.add(path, chain);
			/*
			 * 打印基本调试信息
			 */
			if (log.isDebugEnabled()) {
				// 打印路径
				String[] paths = ai.getPaths();
				StringBuilder sb = new StringBuilder();
				if (null != paths && paths.length > 0) {
					sb.append("   '").append(paths[0]).append("'");
					for (int i = 1; i < paths.length; i++)
						sb.append(", '").append(paths[i]).append("'");
				} else {
					sb.append("!!!EMPTY!!!");
				}
				// 打印方法名
				Method method = ai.getMethod();
				String str;
				if (null != method)
					str = method.getName() + "(...) : " + method.getReturnType().getSimpleName();
				else
					str = "???";
				log.debugf(	"%s >> %s | @Ok(%s) @Fail(%s) | by %d Filters | (I:%s/O:%s)",
							Strings.alignLeft(sb, 30, ' '),
							str,
							ai.getOkView(),
							ai.getFailView(),
							(null == ai.getFilterInfos() ? 0 : ai.getFilterInfos().length),
							ai.getInputEncoding(),
							ai.getOutputEncoding());
			}
		}
		// 记录一个 @At.key
		if (!Strings.isBlank(ai.getPathKey()))
			config.getAtMap().add(ai.getPathKey(), ai.getPaths()[0]);
	}

	public ActionChain get(ActionContext ac, HttpServletRequest req) {
		return root.get(ac, Mvcs.getRequestPath(req));
	}

}
