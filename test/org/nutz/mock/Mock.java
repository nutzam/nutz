package org.nutz.mock;

import java.io.File;

import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mock.servlet.MockHttpSession;
import org.nutz.mock.servlet.MockServletConfig;
import org.nutz.mock.servlet.MockServletContext;
import org.nutz.mock.servlet.multipart.MultipartInputStream;

/**
 * 一些方面的静态方法
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Mock {

	public static class servlet {
		public static MockServletContext context() {
			return new MockServletContext();
		}

		public static MockServletConfig config(String s) {
			return new MockServletConfig(context(), s);
		}

		public static MockHttpServletRequest request() {
			return new MockHttpServletRequest();
		}

		public static MockHttpSession session(MockServletContext context) {
			return new MockHttpSession(context);
		}

		public static MultipartInputStream insmulti(String boundary) {
			return new MultipartInputStream(boundary);
		}

		public static MultipartInputStream insmulti() {
			return insmulti("------NutzMockHTTPBoundary@"
							+ Long.toHexString(System.currentTimeMillis()));
		}

		public static MultipartInputStream insmulti(File... files) {
			MultipartInputStream ins = insmulti();
			for (int i = 0; i < files.length; i++){
				if (files[i].isFile())
					ins.append("F" + i, files[i]);
			}
			return ins;
		}
	}

}
