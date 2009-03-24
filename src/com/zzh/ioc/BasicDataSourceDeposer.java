package com.zzh.ioc;

import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

import com.zzh.lang.Lang;

public class BasicDataSourceDeposer extends ObjectDeposer<BasicDataSource> {

	public BasicDataSourceDeposer() {
		this("dataSource");
	}

	public BasicDataSourceDeposer(String name) {
		super(name);
	}

	@Override
	protected void depose(BasicDataSource obj) {
		try {
			obj.close();
		} catch (SQLException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
