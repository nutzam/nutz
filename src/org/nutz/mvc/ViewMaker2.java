package org.nutz.mvc;

public interface ViewMaker2 extends ViewMaker {

	/**
	 * 增强版的ViewMaker
	 */
	View make(NutConfig conf, ActionInfo ai, String type, String value);
}
