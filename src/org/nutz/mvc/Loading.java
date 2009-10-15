package org.nutz.mvc;

import java.util.Map;

import org.nutz.ioc.Ioc;

public interface Loading {

	void load(Class<?> klass);

	UrlMap getUrls();

	Ioc getIoc();

	Map<String, Map<String, String>> getMessageMap();
}