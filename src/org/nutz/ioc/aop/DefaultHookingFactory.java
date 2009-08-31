package org.nutz.ioc.aop;

import java.lang.reflect.Modifier;

import org.nutz.aop.MethodListener;
import org.nutz.aop.MethodMatcher;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class DefaultHookingFactory implements ObjectHookingFactory {

	public ObjectHooking[] getHooking(AopItem ai) {
		MethodListener ml = null;
		if (ai.getArgs() == null)
			try {
				ml = ai.getType().newInstance();
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		else
			ml = Mirror.me(ai.getType()).born(ai.getArgs());

		ObjectHooking[] array = new ObjectHooking[ai.getHooks().length];
		for (int i = 0; i < ai.getHooks().length; i++) {
			AopHook hook = ai.getHooks()[i];
			ObjectMatcher om = evalObjectMatcher(hook);
			ObjectMethodHooking[] omhs = evalObjectMethodHookings(ml, hook);
			// store it to array
			array[i] = new ObjectHooking(om, omhs);
		}
		return array;
	}

	private ObjectMethodHooking[] evalObjectMethodHookings(MethodListener ml, AopHook hook) {
		AopHookMethod[] hms = hook.getMethods();
		ObjectMethodHooking[] array = new ObjectMethodHooking[hms.length];
		for (int j = 0; j < hms.length; j++) {
			AopHookMethod hookMethod = hms[j];
			MethodMatcher mtdMatcher = evalMethodMatcher(hookMethod);
			// store it to array for return
			array[j] = new ObjectMethodHooking(mtdMatcher, ml);
		}
		return array;
	}

	protected MethodMatcher evalMethodMatcher(AopHookMethod hm) {
		MethodMatcher mtdMatcher = null;
		if (null == hm.getAccess() || AopHookMethod.ACCESS.ALL == hm.getAccess()) {
			mtdMatcher = new MethodMatcher(hm.getRegex(), hm.getIgnore());
		} else if (AopHookMethod.ACCESS.PUBLIC == hm.getAccess()) {
			mtdMatcher = new MethodMatcher(hm.getRegex(), hm.getIgnore(), Modifier.PUBLIC);
		} else if (AopHookMethod.ACCESS.PROTECTED == hm.getAccess()) {
			mtdMatcher = new MethodMatcher(hm.getRegex(), hm.getIgnore(), Modifier.PROTECTED);
		}
		return mtdMatcher;
	}

	protected ObjectMatcher evalObjectMatcher(AopHook hook) {
		ObjectMatcher om = null;
		if (null == hook.getMode() || AopHook.MODE.OBJECT_NAME == hook.getMode()) {
			om = new ObjectMatcher.ByName(hook.getRegex());
		} else if (AopHook.MODE.OBJECT_TYPE == hook.getMode()) {
			om = new ObjectMatcher.ByType(hook.getRegex());
		} else {
			throw Lang.makeThrow("Unknown AopHook.Mode '%s'", hook.getMode().name());
		}
		return om;
	}

}
