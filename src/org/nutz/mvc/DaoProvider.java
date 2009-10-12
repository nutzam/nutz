package org.nutz.mvc;

import javax.servlet.ServletConfig;

import org.nutz.dao.Dao;

public interface DaoProvider {

	Dao create(ServletConfig config);

}
