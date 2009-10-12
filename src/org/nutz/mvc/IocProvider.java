package org.nutz.mvc;

import javax.servlet.ServletConfig;

import org.nutz.ioc.Ioc;

public interface IocProvider {

	Ioc create(ServletConfig config, String[] args);

}
