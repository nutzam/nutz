package org.nutz.mvc.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.Segments;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.ObjectInfo;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.adaptor.PairAdaptor;
import org.nutz.mvc.impl.processor.*;
import org.nutz.mvc.view.VoidView;

public class NutActionChainMaker implements ActionChainMaker {

	public ActionChain eval(NutConfig config, ActionInfo ai) {
		/*
		 * 正常流程
		 */
		List<Processor> list = new ArrayList<Processor>(7);
		// 1. 更新 request 属性
		list.add(new UpdateRequestAttributesProcessor());

		// 2. 修改编码
		list.add(new EncodingProcessor(ai.getInputEncoding(), ai.getOutputEncoding()));

		// 3. 准备调用
		if (Strings.isBlank(ai.getInjectName())) {
			try {
				list.add(new StaticModuleProcessor(ai.getModuleType().newInstance(), ai.getMethod()));
			}
			catch (Exception e) {
				throw Lang.wrapThrow(	e,
										"Fail to create module '%s' by default constructor",
										ai.getModuleType());
			}
		} else {
			list.add(new DynamicModuleProcessor(ai.getModuleType(),
												ai.getInjectName(),
												ai.getMethod()));
		}

		// 4. 过滤
		ObjectInfo<? extends ActionFilter>[] filterInfos = ai.getFilterInfos();
		if (null != filterInfos) {
			ActionFilter[] filters = new ActionFilter[filterInfos.length];
			for (int i = 0; i < filters.length; i++) {
				filters[i] = evalObj(config, filterInfos[i]);
			}
			list.add(new ActionFiltersProcessor(filters));
		}

		// 5. 适配
		list.add(new AdaptorProcessor(evalHttpAdaptor(config, ai)));

		// 6. 调用
		list.add(new MethodInvokeProcessor());

		// 7. 成功视图
		list.add(new ViewProcessor(evalView(config, ai, ai.getOkView())));

		/*
		 * 错误流程
		 */
		Processor errorProcessor = new ViewProcessor(evalView(config, ai, ai.getFailView()));

		/*
		 * 返回动作链实例
		 */
		return new NutActionChain(list, errorProcessor);
	}

	private HttpAdaptor evalHttpAdaptor(NutConfig config, ActionInfo ai) {
		HttpAdaptor re = evalObj(config, ai.getAdaptorInfo());
		if (null == re)
			re = new PairAdaptor();
		re.init(ai.getMethod());
		return re;
	}

	private static <T> T evalObj(NutConfig config, ObjectInfo<T> info) {
		return null == info ? null : Loadings.evalObj(config, info.getType(), info.getArgs());
	}

	private static View evalView(NutConfig config, ActionInfo ai, String viewType) {
		if (Strings.isBlank(viewType))
			return new VoidView();

		String str = Segments.replace(viewType, config.getLoadingContext());
		int pos = str.indexOf(':');
		String type, value;
		if (pos > 0) {
			type = Strings.trim(str.substring(0, pos).toLowerCase());
			value = Strings.trim(pos >= (str.length() - 1) ? null : str.substring(pos + 1));
		} else {
			type = str;
			value = null;
		}
		for (ViewMaker maker : ai.getViewMakers()) {
			View view = maker.make(config.getIoc(), type, value);
			if (null != view)
				return view;
		}
		throw Lang.makeThrow("Can not eval %s(\"%s\") View for %s", viewType, str, ai.getMethod());
	}

}
