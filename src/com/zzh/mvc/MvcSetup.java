package com.zzh.mvc;

import javax.servlet.ServletConfig;

public interface MvcSetup {

	MvcSupport init(ServletConfig config) throws Exception;
}
