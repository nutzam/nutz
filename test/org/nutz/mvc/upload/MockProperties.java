package org.nutz.mvc.upload;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;

public class MockProperties {

	public static Properties getMockProperties() {
		Properties p = new Properties();
		try {
			File file = Files.findFile("org/nutz/mvc/upload/mock.properties");
			p.load(new FileInputStream(file));
		} catch (Exception e) {
			Lang.makeThrow("Can not find \"mock.properties\"");
		}
		return p;
	}

}
