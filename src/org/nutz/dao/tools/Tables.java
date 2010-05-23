package org.nutz.dao.tools;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.tools.annotation.ColType;
import org.nutz.dao.tools.annotation.NotNull;
import org.nutz.dao.tools.impl.NutDTableParser;
import org.nutz.dao.tools.impl.TableDefinitionImpl;
import org.nutz.dao.tools.impl.expert.MysqlExpert;
import org.nutz.dao.tools.impl.expert.OracleExpert;
import org.nutz.dao.tools.impl.expert.PsqlExpert;
import org.nutz.dao.tools.impl.expert.SqlServerExpert;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

/**
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * 
 */
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

	public static List<DTable> load(String dods) {
		DTableParser parser = new NutDTableParser();
		List<DTable> dts = parser.parse(dods);
		return dts;
	}

	public static List<DTable> loadFrom(String dodPath) {
		String dods = Lang.readAll(Streams.fileInr(dodPath));
		return load(dods);
	}

	public static List<DTable> load(File dodFile) {
		String dods = Lang.readAll(Streams.fileInr(dodFile));
		return load(dods);
	}

	public static void define(Dao dao, File dodFile) {
		List<DTable> dts = load(dodFile);
		define(dao, dts);
	}

	public static void define(Dao dao, DTable... dts) {
		List<DTable> dtList = new ArrayList<DTable>(dts.length);
		for (DTable dt : dts)
			dtList.add(dt);
		define(dao, dtList);
	}

	public static void define(Dao dao, String... paths) {
		for (String path : paths) {
			List<DTable> dts = Tables.loadFrom(path);
			Tables.define(dao, dts);
		}
	}

	public static void define(Dao dao, List<DTable> dts) {
		TableDefinition maker = newInstance(dao.meta());
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

	public static DTable parse(Class<?> type) {
		DTable dt = new DTable();
		Table table = type.getAnnotation(Table.class);
		dt.setName(null == table ? type.getSimpleName() : table.value());
		Mirror<?> me = Mirror.me(type);
		Field[] fields = me.getFields();
		// Found ID
		for (Field f : fields) {
			Id id = f.getAnnotation(Id.class);
			if (null != id) {
				DField df = new DField();
				df.setPrimaryKey(true);
				if (id.auto())
					df.setAutoIncreament(true);
				setupFieldAttribute(f, df);
				dt.addField(df);
				break;
			}
		}
		// Found Name
		for (Field f : fields) {
			Name id = f.getAnnotation(Name.class);
			if (null != id) {
				DField df = new DField();
				if (dt.getPks().isEmpty())
					df.setPrimaryKey(true);
				else {
					df.setNotNull(true);
					df.setUnique(true);
				}
				setupFieldAttribute(f, df);
				dt.addField(df);
				break;
			}
		}
		// Found combo PKs
		NutMap pkMap = new NutMap();
		if (dt.getPks().isEmpty()) {
			PK pkanns = type.getAnnotation(PK.class);
			if (null != pkanns) {
				try {
					for (String name : pkanns.value()) {
						Field f = me.getField(name);
						pkMap.put(name, f);
						DField df = new DField();
						df.setPrimaryKey(true);
						df.setNotNull(true);
						setupFieldAttribute(f, df);
						dt.addField(df);
					}
				}
				catch (NoSuchFieldException e) {
					throw Lang.wrapThrow(e);
				}
			}
		}
		// For all fields
		for (Field f : fields) {
			if (null != f.getAnnotation(Id.class)
				|| null != f.getAnnotation(Name.class)
				|| pkMap.containsKey(f.getName()))
				continue;
			if (null == f.getAnnotation(Column.class))
				continue;
			DField df = new DField();
			setupFieldAttribute(f, df);
			dt.addField(df);
		}
		return dt;
	}

	private static void setupFieldAttribute(Field f, DField df) {
		Column col = f.getAnnotation(Column.class);
		if (null == col || Strings.isBlank(col.value()))
			df.setName(f.getName());
		else
			df.setName(col.value());

		if (null != f.getAnnotation(NotNull.class))
			df.setNotNull(true);

		Default def = f.getAnnotation(Default.class);
		if (null != def)
			df.setDefaultValue(def.value());

		ColType colType = f.getAnnotation(ColType.class);
		if (null != colType) {
			df.setType(colType.value());
		} else {
			Mirror<?> ft = Mirror.me(f.getType());
			if (ft.isStringLike()) {
				df.setType("VARCHAR(50)");
			} else if (ft.isIntLike()) {
				df.setType("INT");
			} else if (ft.isDateTimeLike()) {
				df.setType("TIMESTAMP");
			} else if (ft.isBoolean()) {
				df.setType("BOOLEAN");
			} else if (ft.isEnum()) {
				df.setType("VARCHAR(20)");
			} else {
				df.setType("???");
			}
		}
	}
}
