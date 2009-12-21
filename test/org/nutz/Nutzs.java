package org.nutz;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.nutz.aop.ClassDefiner;
import org.nutz.aop.DefaultClassDefiner;
import org.nutz.dao.DatabaseMeta;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

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
		return Strings.trim(pp.getProperty("driver"));
	}

	public static String getUrl() {
		checkProperties();
		return Strings.trim(pp.getProperty("url"));
	}

	public static String getPassword() {
		checkProperties();
		return Strings.trim(pp.getProperty("password"));
	}

	public static String getUserName() {
		checkProperties();
		return Strings.trim(pp.getProperty("username"));
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
		notSupport(format("[%S] don't support this test", meta.getTypeName()));
	}

	public static ClassDefiner cd() {
		return new DefaultClassDefiner();
	}
}
