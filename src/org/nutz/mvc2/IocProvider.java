package org.nutz.mvc2;

import javax.servlet.ServletConfig;

import org.nutz.ioc.Ioc;

public interface IocProvider {

	Ioc create(ServletConfig config, String[] args);

}
