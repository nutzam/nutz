package org.nutz.mvc;

import javax.servlet.ServletConfig;

import org.nutz.ioc.Ioc;

public class IocMvc implements MvcSupport {

	public IocMvc(Ioc ioc, ServletConfig config) {
		this.ioc = ioc;
		ioc.add(new JspViewMaker()).add(new RedirectViewMaker());
		ioc.add(new ServerFileMaker(config.getServletContext()));
		ioc.add(new ServerAttributeMaker(config.getServletContext()));
		ioc.add(new ServerConfigMaker(config));
	}

	private Ioc ioc;

	public Url getUrl(String path) throws UrlNotFoundException {
		return ioc.get(Url.class, path);
	}

}
