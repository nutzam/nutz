package org.nutz.mvc2;

import javax.servlet.ServletConfig;

import org.nutz.dao.Dao;

public interface DaoProvider {

	Dao getDataSource(ServletConfig config);

}
