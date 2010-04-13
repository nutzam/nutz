package org.nutz.mock;

import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mock.servlet.MockServletConfig;
import org.nutz.mock.servlet.MockServletContext;
import org.nutz.mock.servlet.multipart.MultipartInputStream;

public abstract class Mock {

	public static class servlet {
		public static MockServletContext context() {
			return new MockServletContext();
		}

		public static MockServletConfig config(String s) {
			return new MockServletConfig(context(), s);
		}

		public static MockHttpServletRequest request() {
			return new MockHttpServletRequest(context());
		}

		public static MultipartInputStream ins(String boundary) {
			return new MultipartInputStream(boundary);
		}

		public static MultipartInputStream ins() {
			return ins("------NutzMockHTTPBoundary@" + Long.toHexString(System.currentTimeMillis()));
		}

	}

}
