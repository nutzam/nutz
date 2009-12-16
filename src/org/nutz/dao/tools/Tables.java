package org.nutz.dao.tools;

import java.io.File;
import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.tools.impl.NutDTableParser;
import org.nutz.dao.tools.impl.TableDefinitionImpl;
import org.nutz.dao.tools.impl.expert.*;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public abstract class Tables {

	public static TableDefinition newInstance(DatabaseMeta db) {
		if (db.isOracle()) {
			return new TableDefinitionImpl(new OracleExpert());
		} else if (db.isMySql()) {
			return new TableDefinitionImpl(new MysqlExpert());
		} else if (db.isPostgresql()) {
			return new TableDefinitionImpl(new PsqlExpert());
		} else if (db.isSqlServer()) {
			return new TableDefinitionImpl(new SqlServerExpert());
		} else if (db.isH2()) {
			return new TableDefinitionImpl(new PsqlExpert());
		}
		throw Lang.makeThrow("I don't now how to create table for '%s'", db.toString());
	}

	public static void run(Dao dao, List<DTable> dts) {
		TableDefinition maker = newInstance(((NutDao) dao).meta());
		for (DTable dt : dts) {
			Sql sql;
			if (dao.exists(dt.getName())) {
				sql = maker.makeDropSql(dt);
				dao.execute(sql);
			}
			sql = maker.makeCreateSql(dt);
			dao.execute(sql);
		}
	}

	public static List<DTable> defineBy(String dods) {
		DTableParser parser = new NutDTableParser();
		List<DTable> dts = parser.parse(dods);
		return dts;
	}

	public static List<DTable> define(String dodPath) {
		String dods = Lang.readAll(Streams.fileInr(dodPath));
		return defineBy(dods);
	}

	public static List<DTable> define(File dodFile) {
		String dods = Lang.readAll(Streams.fileInr(dodFile));
		return defineBy(dods);
	}

	public static void run(Dao dao, File dodFile) {
		List<DTable> dts = define(dodFile);
		run(dao, dts);
	}

}
