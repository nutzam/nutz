package org.nutz.dao.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.nutz.dao.Chain;
import org.nutz.dao.Condition;
import org.nutz.dao.DaoException;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.PkType;
import org.nutz.dao.impl.jdbc.NutPojo;
import org.nutz.dao.impl.sql.pojo.ConditionPItem;
import org.nutz.dao.impl.sql.pojo.EntityTableNamePItem;
import org.nutz.dao.impl.sql.pojo.EntityViewNamePItem;
import org.nutz.dao.impl.sql.pojo.InsertFieldsPItem;
import org.nutz.dao.impl.sql.pojo.InsertValuesPItem;
import org.nutz.dao.impl.sql.pojo.PkConditionPItem;
import org.nutz.dao.impl.sql.pojo.QueryEntityFieldsPItem;
import org.nutz.dao.impl.sql.pojo.SingleColumnCondtionPItem;
import org.nutz.dao.impl.sql.pojo.SqlTypePItem;
import org.nutz.dao.impl.sql.pojo.StaticPItem;
import org.nutz.dao.impl.sql.pojo.UpdateFieldsByChainPItem;
import org.nutz.dao.impl.sql.pojo.UpdateFieldsPItem;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.PItem;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;
import org.nutz.dao.sql.SqlType;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public abstract class Pojos {

	// ==========================================================
	// 以下是创建 POJO 语句元素的帮助方法
	public static class Items {

		public static PItem sqlType() {
			return new SqlTypePItem();
		}

		public static PItem entityTableName() {
			return new EntityTableNamePItem();
		}

		public static PItem entityViewName() {
			return new EntityViewNamePItem();
		}

		public static PItem wrap(String str) {
			return new StaticPItem(str);
		}

		public static PItem wrapf(String fmt, Object... args) {
			return new StaticPItem(String.format(fmt, args));
		}

		public static PItem insertFields() {
			return new InsertFieldsPItem();
		}

		public static PItem insertValues() {
			return new InsertValuesPItem();
		}

		public static PItem updateFields(Object refer) {
			return new UpdateFieldsPItem(refer);
		}

		public static PItem updateFieldsBy(Chain chain) {
			return new UpdateFieldsByChainPItem(chain);
		}

		public static PItem queryEntityFields() {
			return new QueryEntityFieldsPItem();
		}

		public static PItem cndId(Entity<?> en, Number id) {
		    MappingField mappingField = en.getIdField();
		    if (mappingField == null)
		        throw new DaoException("expect @Id but NOT found. " + en.getType().getName());
			return cndColumn(mappingField, id);
		}

		public static PItem cndName(Entity<?> en, String name) {
		    MappingField mappingField = en.getNameField();
            if (mappingField == null)
                throw new DaoException("expect @Name but NOT found. " + en.getType().getName());
			return cndColumn(mappingField, name);
		}

		public static PItem cndColumn(MappingField mappingField, Object def) {
			SingleColumnCondtionPItem re = new SingleColumnCondtionPItem(mappingField, def);
			re.setCasesensitive(mappingField.isCasesensitive());
			return re;
		}

		public static PItem cndColumn(String colName, MappingField mappingField, Object def) {
			return new SingleColumnCondtionPItem(	colName,
													mappingField.getTypeClass(),
													mappingField.getAdaptor(),
													def);
		}

		public static PItem cndPk(Entity<?> en, Object[] pks) {
			ValueAdaptor[] vas = new ValueAdaptor[en.getCompositePKFields().size()];
			int i = 0;
			for (MappingField mf : en.getCompositePKFields())
				vas[i++] = mf.getAdaptor();
			return new PkConditionPItem(vas, pks);
		}

		public static PItem cndAuto(Entity<?> en, Object obj) {
			obj = Lang.first(obj);
			switch (en.getPkType()) {
			case ID:
				Number id = null != obj ? ((Number) en.getIdField().getValue(obj)) : null;
				return cndId(en, id);
			case NAME:
				String name = null != obj ? en.getNameField().getValue(obj).toString() : null;
				return cndName(en, name);
			case COMPOSITE:
				Object[] pks = null;
				if (null != obj) {
					pks = new Object[en.getCompositePKFields().size()];
					int i = 0;
					for (EntityField ef : en.getCompositePKFields())
						pks[i++] = ef.getValue(obj);
				}
				return cndPk(en, pks);
			default:
				if (Map.class.isAssignableFrom(en.getType())) {
					return null; // Map形式的话,不一定需要主键嘛
				}
				throw Lang.makeThrow("Don't know how to make fetch key %s:'%s'", en.getType()
																					.getName(), obj);
			}
		}

		public static PItem[] cnd(Condition cnd) {
			List<PItem> list = new LinkedList<PItem>();
			if (null == cnd) {}
			// 高级条件
			else if (cnd instanceof Criteria) {
				list.add((Criteria) cnd);
			}
			// 普通条件
			else {
				list.add(new ConditionPItem(cnd));
			}
			return list.toArray(new PItem[list.size()]);
		}

		public static Pager pager(Condition cnd) {
			if (null == cnd) {
				return null;
			}
			// 高级条件
			else if (cnd instanceof Criteria) {
				return ((Criteria) cnd).getPager();
			}
			// 普通条件
			else {
				return null;
			}
		}

	}

	// 以上是创建 POJO 语句元素的帮助方法
	// ==========================================================

	public static Pojo createRun(PojoCallback callback) {
		return new NutPojo().setSqlType(SqlType.RUN).setAfter(callback);
	}

	public static List<MappingField> getFieldsForInsert(Entity<?> en, FieldMatcher fm) {
		List<MappingField> re = new ArrayList<MappingField>(en.getMappingFields().size());
		for (MappingField mf : en.getMappingFields()) {
			if (!mf.isAutoIncreasement() && !mf.isReadonly() && mf.isInsert())
				if (null == fm || fm.match(mf.getName()))
					re.add(mf);
		}
		return re;
	}

	public static List<MappingField> getFieldsForUpdate(Entity<?> en, FieldMatcher fm, Object refer) {
		List<MappingField> re = new ArrayList<MappingField>(en.getMappingFields().size());
		for (MappingField mf : en.getMappingFields()) {
			if (mf.isPk()) {
				if (en.getPkType() == PkType.ID && mf.isId())
					continue;
				if (en.getPkType() == PkType.NAME && mf.isName())
					continue;
				if (en.getPkType() == PkType.COMPOSITE && mf.isCompositePk())
					continue;
			}
			if (mf.isReadonly() || mf.isAutoIncreasement() || !mf.isUpdate())
				continue;
			else if (null != fm && null != refer && fm.isIgnoreNull() && null == mf.getValue(refer))
				continue;
			if (null == fm || fm.match(mf.getName()))
				re.add(mf);
		}
		return re;
	}

	private static final Pattern ptn = Pattern.compile(	"^(WHERE|ORDER BY)(.+)",
														Pattern.CASE_INSENSITIVE);

	public static String formatCondition(Entity<?> en, Condition cnd) {
		if (null != cnd) {
			String str = Strings.trim(cnd.toSql(en));
			if (!ptn.matcher(str).find())
				return "WHERE " + str;
			return str;
		}
		return "";
	}

	public static Pojo pojo(JdbcExpert expert, Entity<?> en, SqlType type) {
		Pojo pojo = expert.createPojo(type);
		pojo.getContext().setFieldMatcher(FieldFilter.get(en.getType()));
		return pojo;
	}

}
