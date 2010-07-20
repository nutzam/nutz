package org.nutz.mvc;

import java.util.Map;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.init.NutConfig;

public interface Loading {

	void load(NutConfig config, Class<?> klass);

	UrlMap getUrls();

	Ioc getIoc();

	Map<String, Map<String, String>> getMessageMap();
}