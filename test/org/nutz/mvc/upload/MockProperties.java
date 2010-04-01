package org.nutz.mvc.upload;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;

public class MockProperties {

	private static Properties mockProperties;

	public static void setMockProperties(Properties p) {
		mockProperties = p;
	}

	public static Properties getMockProperties() {
		if (null != mockProperties)
			return mockProperties;
		Properties p = new Properties();
		try {
			File file = Files.findFile("org/nutz/mvc/upload/mock.properties");
			p.load(new FileInputStream(file));
		}
		catch (Exception e) {
			Lang.makeThrow("Can not find \"mock.properties\"");
		}
		return p;
	}

	private static Integer bufferIn;

	public static int getBufferIn() {
		if (null == bufferIn) {
			int bin = 0;
			try {
				bin = Integer.valueOf(getMockProperties().getProperty("buffer.in"));
			}
			catch (NumberFormatException e) {}
			bufferIn = Integer.valueOf(bin);
		}
		return bufferIn.intValue();
	}

}
