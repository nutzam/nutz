package org.nutz.ioc.aop;

import java.util.HashMap;
import java.util.Map;

import org.nutz.aop.MethodMatcher;
import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.LogFormat;
import org.nutz.log.LogOutput;
import org.nutz.log.aop.LogListener;
import org.nutz.log.file.FileLogOutput;

public class LogHookingFactory extends DefaultHookingFactory {

	@Override
	public ObjectHooking[] getHooking(AopItem ai) {
		ObjectHooking[] array = new ObjectHooking[ai.getHooks().length];
		for (int i = 0; i < ai.getHooks().length; i++) {
			AopHook hook = ai.getHooks()[i];
			ObjectMatcher om = evalObjectMatcher(hook);
			ObjectMethodHooking[] omhs = evalObjectMethodHookings(ai.getInit(), hook);
			// store it to array
			array[i] = new ObjectHooking(om, omhs);
		}
		return array;
	}

	private ObjectMethodHooking[] evalObjectMethodHookings(Map<String, Object> init, AopHook hook) {
		AopHookMethod[] hms = hook.getMethods();
		ObjectMethodHooking[] array = new ObjectMethodHooking[hms.length];
		for (int j = 0; j < hms.length; j++) {
			AopHookMethod hookMethod = hms[j];
			MethodMatcher mtdMatcher = evalMethodMatcher(hookMethod);
			/*
			 * So that's mean each hook method can output to differenct file
			 */
			Map<String, Object> map = mergeConfigMap(init, hook, hookMethod);
			LogOutput output = evalLogOutput(map);
			LogFormat format = evalLogFormat(init, hook, hookMethod);
			/*
			 * Just use Log.INFO for temporary
			 */
			Log log = new Log(Log.INFO, output, format);
			LogListener ml = new LogListener(log);
			// setup ml by map
			if (map.containsKey("deep"))
				ml.setDeep((Integer) map.get("deep"));
			if (map.containsKey("re"))
				ml.setShowReturn((Boolean) map.get("re"));
			if (map.containsKey("args"))
				ml.setShowArgs(Castors.me().castTo(map.get("args"), int[].class));

			// store it to array for return
			array[j] = new ObjectMethodHooking(mtdMatcher, ml);
		}
		return array;
	}

	private LogFormat evalLogFormat(Map<String, Object> init, AopHook hook, AopHookMethod hookMethod) {
		LogFormat format = null;
		Map<String, Object> fmtMap = new HashMap<String, Object>();
		addFormatMap(fmtMap, init);
		addFormatMap(fmtMap, hook.getConfig());
		addFormatMap(fmtMap, hookMethod.getConfig());
		boolean showThread = true;
		if (fmtMap.containsKey("showThread"))
			showThread = (Boolean) fmtMap.get("showThread");
		String timePattern = "yy-MM-dd hh-mm-ss.SSS";
		if (fmtMap.containsKey("pattern"))
			timePattern = fmtMap.get("pattern").toString();
		format = LogFormat.create(showThread, timePattern);
		if (fmtMap.containsKey("width"))
			format.setWidth((Integer) fmtMap.get("width"));
		return format;
	}

	@SuppressWarnings("unchecked")
	private static void addFormatMap(Map<String, Object> fmtMap, Map<String, Object> map) {
		if (null != map)
			if (map.containsKey("format"))
				fmtMap.putAll((Map<? extends String, ? extends Object>) map.get("format"));
	}

	@SuppressWarnings("unchecked")
	private LogOutput evalLogOutput(Map<String, Object> map) {
		LogOutput output = null;
		String outputClassName = (String) map.get("output");
		if (null == outputClassName)
			outputClassName = FileLogOutput.class.getName();
		try {
			Mirror<LogOutput> outputType = (Mirror<LogOutput>) Mirror.me(Class
					.forName(outputClassName));
			output = outputType.born(map.get("file"));
			output.setup(map);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		return output;
	}

	private Map<String, Object> mergeConfigMap(Map<String, Object> init, AopHook hook,
			AopHookMethod hookMethod) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (null != init)
			map.putAll(init);
		if (null != hook.getConfig())
			map.putAll(hook.getConfig());
		if (null != hookMethod.getConfig())
			map.putAll(hookMethod.getConfig());
		return map;
	}

}
