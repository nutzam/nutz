package com.zzh.mvc;

import java.io.File;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.zzh.ioc.Nut;
import com.zzh.ioc.ObjectMaker;
import com.zzh.mvc.view.JspView;
import com.zzh.mvc.view.RedirectView;

public class NutMvc implements MvcSupport {

	private static class JspViewMaker extends ObjectMaker<View> {
		@Override
		protected boolean accept(Map<String, Object> properties) {
			return properties.containsKey("jsp");
		}

		@Override
		protected View make(Map<String, Object> properties) {
			return new JspView(properties.get("jsp").toString());
		}
	}

	private static class RedirectViewMaker extends ObjectMaker<View> {
		@Override
		protected boolean accept(Map<String, Object> properties) {
			return properties.containsKey("redirect");
		}

		@Override
		protected View make(Map<String, Object> properties) {
			return new RedirectView(properties.get("redirect").toString());
		}
	}

	private static class ServerFileMaker extends ObjectMaker<File> {

		private ServletContext context;

		public ServerFileMaker(ServletContext context) {
			this.context = context;
		}

		@Override
		protected boolean accept(Map<String, Object> properties) {
			return properties.containsKey("file");
		}

		@Override
		protected File make(Map<String, Object> properties) {
			String path = context.getRealPath(properties.get("file").toString());
			return new File(path);
		}
	}

	private static class ServerAttributeMaker extends ObjectMaker<Object> {

		private ServletContext context;

		public ServerAttributeMaker(ServletContext context) {
			this.context = context;
		}

		@Override
		protected boolean accept(Map<String, Object> properties) {
			return properties.containsKey("server");
		}

		@Override
		protected Object make(Map<String, Object> properties) {
			return context.getAttribute(properties.get("server").toString());
		}
	}

	private static class ServerConfigMaker extends ObjectMaker<String> {

		private ServletConfig config;

		public ServerConfigMaker(ServletConfig config) {
			this.config = config;
		}

		@Override
		protected boolean accept(Map<String, Object> properties) {
			return properties.containsKey("config");
		}

		@Override
		protected String make(Map<String, Object> properties) {
			String name = properties.get("server").toString();
			if ("$server-name".equals(name))
				return config.getServletName();
			return config.getInitParameter(name);
		}
	}

	public NutMvc(Nut nut, ServletConfig config) {
		this.nut = nut;
		nut.add(new JspViewMaker()).add(new RedirectViewMaker());
		nut.add(new ServerFileMaker(config.getServletContext()));
		nut.add(new ServerAttributeMaker(config.getServletContext()));
		nut.add(new ServerConfigMaker(config));
	}

	private Nut nut;

	@Override
	public Url getUrl(String path) throws UrlNotFoundException {
		return nut.get(Url.class, path);
	}

}
