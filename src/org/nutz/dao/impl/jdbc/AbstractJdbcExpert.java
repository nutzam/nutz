package org.nutz.dao.impl.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.LinkField;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.impl.entity.field.ManyManyLinkField;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlType;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 对于所有数据库的抽象实现
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class AbstractJdbcExpert implements JdbcExpert {

	private static final Log log = Logs.get();

	/**
	 * 提供给子类使用的配置文件对象
	 */
	protected JdbcExpertConfigFile conf;

	// ====================================================================
	// 构造函数：子类需要将重载
	public AbstractJdbcExpert(JdbcExpertConfigFile conf) {
		this.conf = conf;
	}

	// ====================================================================
	// 下面为子类默认实现几个接口函数

	public void setupEntityField(Connection conn, Entity<?> en) {
		Statement stat = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		try {
			// 获取数据库元信息
			stat = conn.createStatement();
			rs = stat.executeQuery(createResultSetMetaSql(en));
			rsmd = rs.getMetaData();
			// 循环字段检查
			for (MappingField mf : en.getMappingFields()) {
				int ci = Daos.getColumnIndex(rsmd, mf.getColumnName());
				// 是否只读，如果人家已经是指明是只读了，那么就不要自作聪明得再从数据库里验证了
//				if (!mf.isReadonly() && rsmd.isReadOnly(ci))
//					mf.setAsReadonly();
				// 是否非空
				if (ResultSetMetaData.columnNoNulls == rsmd.isNullable(ci))
					mf.setAsNotNull();
				// 枚举类型在数据库中的值
				if (mf.getTypeMirror().isEnum()) {
					if (Daos.isIntLikeColumn(rsmd, ci)) {
						mf.setColumnType(ColType.INT);
					} else {
						mf.setColumnType(ColType.VARCHAR);
					}
				}
			}
		}
		catch (Exception e) {
			if (log.isDebugEnabled())
				log.debugf("Table '%s' doesn't exist!", en.getViewName());
		}
		// Close ResultSet and Statement
		finally {
			Daos.safeClose(stat, rs);
		}
	}

	public ValueAdaptor getAdaptor(MappingField ef) {
		Mirror<?> mirror = ef.getTypeMirror();
		// 为数字型枚举的特殊判断
		if (mirror.isEnum() && ColType.INT == ef.getColumnType())
			return Jdbcs.Adaptor.asEnumInt;
		// 用普通逻辑返回适配器
		return Jdbcs.getAdaptor(mirror);
	}

	public Pojo createPojo(SqlType type) {
		return new NutPojo().setSqlType(type);
	}

	public boolean dropEntity(Dao dao, Entity<?> en) {
		String tableName = en.getTableName();
		String viewName = en.getViewName();

		try {
			dropRelation(dao, en);
			if (tableName.equals(viewName)) {
				dao.execute(Sqls.create("DROP TABLE " + tableName));
			} else {
				dao.execute(Sqls.create("DROP VIEW " + viewName),
							Sqls.create("DROP TABLE " + tableName));
			}
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	public Map<String, Object> getConf() {
		return this.conf.getConfig();
	}

	// ====================================================================
	// 下面是提供给子类使用的一些帮助函数

	protected String createResultSetMetaSql(Entity<?> en) {
		return "SELECT * FROM " + en.getViewName();
	}

	protected void createRelation(Dao dao, Entity<?> en) {
		final List<Sql> sqls = new ArrayList<Sql>(5);
		for (LinkField lf : en.visitManyMany(null, null, null)) {
			ManyManyLinkField mm = (ManyManyLinkField) lf;
			if (dao.exists(mm.getRelationName()))
				continue;
			String sql = "CREATE TABLE " + mm.getRelationName() + "(";
			sql += mm.getFromColumnName() + " " + evalFieldType(mm.getHostField()) + ",";
			sql += mm.getToColumnName() + " " + evalFieldType(mm.getLinkedField());
			sql += ")";
			sqls.add(Sqls.create(sql));
		}
		dao.execute(sqls.toArray(new Sql[sqls.size()]));
	}

	protected void dropRelation(Dao dao, Entity<?> en) {
		final List<Sql> sqls = new ArrayList<Sql>(5);
		for (LinkField lf : en.visitManyMany(null, null, null)) {
			ManyManyLinkField mm = (ManyManyLinkField) lf;
			if (!dao.exists(mm.getRelationName()))
				continue;
			sqls.add(Sqls.create("DROP TABLE " + mm.getRelationName()));
		}
		dao.execute(sqls.toArray(new Sql[sqls.size()]));
	}

	protected String evalFieldType(MappingField mf) {
		switch (mf.getColumnType()) {
		case CHAR:
			return "CHAR(" + mf.getWidth() + ")";

		case BOOLEAN:
			return "BOOLEAN";

		case VARCHAR:
			return "VARCHAR(" + mf.getWidth() + ")";

		case TEXT:
			return "TEXT";

		case BINARY:
			return "BLOB";

		case TIMESTAMP:
			return "TIMESTAMP";

		case DATETIME:
			return "DATETIME";

		case DATE:
			return "DATE";
		case TIME:
			return "TIME";

		case INT:
			// 用户自定义了宽度
			if (mf.getWidth() > 0)
				return "INT(" + mf.getWidth() + ")";
			// 用数据库的默认宽度
			return "INT";

		case FLOAT:
			// 用户自定义了精度
			if (mf.getWidth() > 0 && mf.getPrecision() > 0) {
				return "NUMERIC(" + mf.getWidth() + "," + mf.getPrecision() + ")";
			}
			// 用默认精度
			if (mf.getTypeMirror().isDouble())
				return "NUMERIC(15,10)";
			return "FLOAT";
		}
		throw Lang.makeThrow(	"Unsupport colType '%s' of field '%s' in '%s' ",
								mf.getColumnType(),
								mf.getName(),
								mf.getEntity().getType().getName());
	}

	protected static List<DaoStatement> wrap(String... sqls) {
		List<DaoStatement> sts = new ArrayList<DaoStatement>(sqls.length);
		for (String sql : sqls)
			if (!Strings.isBlank(sql))
				sts.add(Sqls.create(sql));
		return sts;
	}

	protected static List<DaoStatement> wrap(List<String> sqls) {
		List<DaoStatement> sts = new ArrayList<DaoStatement>(sqls.size());
		for (String sql : sqls)
			if (!Strings.isBlank(sql))
				sts.add(Sqls.create(sql));
		return sts;
	}

	protected static String gSQL(String ptn, String table, String field) {
		CharSegment cs = new CharSegment(ptn);
		cs.set("T", table).set("F", field);
		return cs.toString();
	}
}
