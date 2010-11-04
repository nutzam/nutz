package org.nutz.dao.tools;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.tools.impl.NutDTableParser;
import org.nutz.dao.tools.impl.TableDefinitionImpl;
import org.nutz.dao.tools.impl.convert.DefaultFieldTypeMapping;
import org.nutz.dao.tools.impl.convert.FieldTypeMapping;
import org.nutz.dao.tools.impl.convert.OracleFieldTypeMapping;
import org.nutz.dao.tools.impl.expert.MysqlExpert;
import org.nutz.dao.tools.impl.expert.OracleExpert;
import org.nutz.dao.tools.impl.expert.PsqlExpert;
import org.nutz.dao.tools.impl.expert.SqlServerExpert;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.util.NutMap;

/**
 * 处理dod文件的辅助类
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public abstract class Tables {

	/**
	 * 根据数据库类型,生成相应的TableDefinition
	 * @param db 数据库数据
	 * @return 相应的TableDefinition
	 */
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

	/**
	 * 生成DTable列表
	 */
	public static List<DTable> load(String dods) {
		DTableParser parser = new NutDTableParser();
		List<DTable> dts = parser.parse(dods);
		return dts;
	}

	/**
	 * 从指定路径读取dod文件,然后生成相应的DTable列表
	 */
	public static List<DTable> loadFrom(String dodPath) {
		String dods = Lang.readAll(Streams.fileInr(dodPath));
		return load(dods);
	}

	/**
	 * 从指定文件读取,然后生成相应的DTable列表
	 */
	public static List<DTable> load(File dodFile) {
		String dods = Lang.readAll(Streams.fileInr(dodFile));
		return load(dods);
	}

	/**
	 * 根据指定的文件,生成相应的表
	 * <p/><b>如果表已经存在,则删除!</b>
	 */
	public static void define(Dao dao, File dodFile) {
		List<DTable> dts = load(dodFile);
		define(dao, dts);
	}

	/**
	 * 根据指定的文件,生成相应的表
	 * <p/><b>如果表已经存在,则删除!</b>
	 */
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

	/**
	 * 根据定义生成相应的表
	 * <p/><b>如果表已经存在,则删除!</b>
	 */
	public static void define(Dao dao, List<DTable> dts) {
		TableDefinition maker = newInstance(dao.meta());
		for (DTable dt : dts) {
			if (dao.exists(dt.getName()))
				dao.execute(maker.makeDropSql(dt));
			dao.execute(maker.makeCreateSql(dt));
		}
	}
	
	/**
	 * 根据定义生成相应的表,如果表已经存在,则跳过
	 */
	public static void defineSafe(Dao dao, List<DTable> dts) {
		TableDefinition maker = newInstance(dao.meta());
		for (DTable dt : dts) {
			if (dao.exists(dt.getName()))
				continue;
			dao.execute(maker.makeCreateSql(dt));
		}
	}
	
	/**
	 * 从类生成DTable定义
	 */
	public static DTable parse(Class<?> type) {
		return parse(type, new DefaultFieldTypeMapping());
	}
	
	/**
	 * 从类生成DTable定义,根据数据库字段判断映射器
	 */
	public static DTable parse(Class<?> type, DatabaseMeta meta) {
		if (meta.isOracle())
			return parse(type, new OracleFieldTypeMapping());
		return parse(type, new DefaultFieldTypeMapping());
	}

	/**
	 * 从类生成DTable定义,通过自定义的字段类型映射器
	 */
	public static DTable parse(Class<?> type, FieldTypeMapping mapping) {
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
				mapping.convert(f, df);
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
				mapping.convert(f, df);
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
						mapping.convert(f, df);
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
			mapping.convert(f, df);
			dt.addField(df);
		}
		return dt;
	}
	
}
