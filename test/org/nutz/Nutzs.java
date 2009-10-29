package org.nutz;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.nutz.dao.DatabaseMeta;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.json.JsonLoader;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;

import static java.lang.String.*;

public class Nutzs {

	private static Properties pp = null;

	private static void checkProperties() {
		if (null == pp)
			try {
				pp = new Properties();
				pp.load(new FileInputStream(Files.findFile("nutz-test.properties")));
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
	}

	public static String getDriver() {
		checkProperties();
		return pp.getProperty("driver");
	}

	public static String getUrl() {
		checkProperties();
		return pp.getProperty("url");
	}

	public static String getPassword() {
		checkProperties();
		return pp.getProperty("password");
	}

	public static String getUserName() {
		checkProperties();
		return pp.getProperty("username");
	}

	private static Map<String, Ioc> nuts = new HashMap<String, Ioc>();

	public static Ioc getIoc(String key) {
		Ioc nut = nuts.get(key);
		if (null == nut) {
			synchronized (Nutzs.class) {
				nut = nuts.get(key);
				try {
					if (null == nut) {
						nut = new NutIoc(new JsonLoader(key));
						nuts.put(key, nut);
					}
				} catch (Exception e) {
					throw Lang.wrapThrow(e);
				}
			}
		}
		return nut;
	}

	public static void depose() {
		for (Ioc ioc : nuts.values())
			ioc.depose();
		nuts.clear();
		nuts = null;
	}

	public static void notSupport(String message) {
	// junit.framework.Assert.fail(message);
	}

	public static void notSupport(DatabaseMeta meta) {
		notSupport(format("[%S] don't support this test", meta.getProductName()));
	}
}
