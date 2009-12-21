package org.nutz.dao.entity.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nutz.dao.Daos;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.TableName;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.FieldType;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.entity.EntityName;
import org.nutz.dao.entity.ErrorEntitySyntaxException;
import org.nutz.dao.entity.Link;
import org.nutz.dao.entity.ValueAdapter;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Next;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Readonly;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.View;
import org.nutz.dao.entity.born.Borns;
import org.nutz.dao.entity.next.FieldQuery;
import org.nutz.dao.entity.next.FieldQuerys;
import org.nutz.dao.sql.FieldAdapter;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * This class must be drop after make() be dropped
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public class DefaultEntityMaker implements EntityMaker {

	private static final Log log = Logs.getLog(DefaultEntityMaker.class);

	public Entity<?> make(DatabaseMeta db, Connection conn, Class<?> type) {
		Entity<?> entity = new Entity<Object>();
		Mirror<?> mirror = Mirror.me(type);
		entity.setMirror(mirror);

		if (log.isDebugEnabled())
			log.debugf("Parse POJO <%s> for DB[%s]", type.getName(), db.getTypeName());

		// Get @Table & @View
		entity.setTableName(evalEntityName(type, Table.class, null));
		entity.setViewName(evalEntityName(type, View.class, Table.class));

		// Borning
		entity.setBorning(Borns.evalBorning(entity));

		// Check if the POJO has @Column fields
		boolean existsColumnAnnField = isPojoExistsColumnAnnField(mirror);

		// Eval PKs
		HashMap<String, EntityField> pkmap = new HashMap<String, EntityField>();
		PK pk = type.getAnnotation(PK.class);
		if (null != pk) {
			for (String pknm : pk.value())
				pkmap.put(pknm, null);
		}

		// Get relative meta data from DB
		Statement stat = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		List<FieldQuery> befores;
		List<FieldQuery> afters;
		try {
			try {
				stat = conn.createStatement();
				rs = stat.executeQuery(db.getResultSetMetaSql(entity.getViewName()));
				rsmd = rs.getMetaData();
			} catch (Exception e) {
				if (log.isWarnEnabled())
					log.warn("Table '" + entity.getViewName() + "' doesn't exist.");
			}

			befores = new ArrayList<FieldQuery>(5);
			afters = new ArrayList<FieldQuery>(5);
			// For each fields ...
			for (Field f : mirror.getFields()) {
				// When the field declared @Many, @One, @ManyMany
				Link link = evalLink(db, conn, mirror, f);
				if (null != link) {
					entity.addLinks(link);
				}
				// Then try to eval the field
				else {
					// Current POJO has @Column field, but current not, ignore
					// it
					if (existsColumnAnnField)
						if (!pkmap.containsKey(f.getName()))
							if (null == f.getAnnotation(Column.class))
								if (null == f.getAnnotation(Id.class))
									if (null == f.getAnnotation(Name.class))
										continue;
					// Create EntityField
					EntityField ef = evalField(db, rsmd, entity, f);

					// Is it a PK?
					if (pkmap.containsKey(ef.getName())) {
						pkmap.put(ef.getName(), ef);
						if (!(ef.isId() || ef.isName()))
							ef.setType(FieldType.PK);
					}

					// Is befores? or afters?
					if (null != ef.getBeforeInsert())
						befores.add(ef.getBeforeInsert());
					else if (null != ef.getAfterInsert())
						afters.add(ef.getAfterInsert());

					// Append to Entity
					if (null != ef) {
						entity.addField(ef);
					}
				}
			} // Done for all fields
		}
		// For exception...
		catch (SQLException e) {
			throw Lang.wrapThrow(e, "Fail to make POJO '%s'", type);
		}
		// Close ResultSet and Statement
		finally {
			Daos.safeClose(stat, rs);
		}

		// Then let's check the pks
		if (pkmap.size() > 0) {
			EntityField[] pks = new EntityField[pkmap.size()];
			for (int i = 0; i < pk.value().length; i++)
				pks[i] = pkmap.get(pk.value()[i]);

			entity.setPkFields(pks);
		}

		// Eval beforeInsert fields and afterInsert fields
		entity.setBefores(befores.toArray(new FieldQuery[befores.size()]));
		entity.setAfters(afters.toArray(new FieldQuery[afters.size()]));

		return entity;
	}

	private ErrorEntitySyntaxException error(Entity<?> entity, String fmt, Object... args) {
		return new ErrorEntitySyntaxException(String.format("[%s] : %s", null == entity ? "NULL"
				: entity.getType().getName(), String.format(fmt, args)));
	}

	private EntityField evalField(	DatabaseMeta db,
									ResultSetMetaData rsmd,
									Entity<?> entity,
									Field field) throws SQLException {
		// Change accessiable
		field.setAccessible(true);
		// Create ...
		EntityField ef = new EntityField(entity, field);

		// Eval field column name
		Column column = field.getAnnotation(Column.class);
		if (null == column || Strings.isBlank(column.value()))
			ef.setColumnName(field.getName());
		else
			ef.setColumnName(column.value());

		int ci = Daos.getColumnIndex(rsmd, ef.getColumnName());

		// @Readonly
		ef.setReadonly((field.getAnnotation(Readonly.class) != null));

		// Not Null
		if (null != rsmd)
			ef.setNotNull(ResultSetMetaData.columnNoNulls == rsmd.isNullable(ci));

		// For Enum field
		if (null != rsmd)
			if (ef.getMirror().isEnum()) {
				if (Daos.isIntLikeColumn(rsmd, ci))
					ef.setType(FieldType.ENUM_INT);
			}

		// @Default
		Default dft = field.getAnnotation(Default.class);
		if (null != dft) {
			ef.setDefaultValue(new CharSegment(dft.value()));
		}

		// @Prev
		Prev prev = field.getAnnotation(Prev.class);
		if (null != prev) {
			ef.setBeforeInsert(FieldQuerys.eval(db, prev.value(), ef));
		}

		// @Next
		Next next = field.getAnnotation(Next.class);
		if (null != next) {
			ef.setAfterInsert(FieldQuerys.eval(db, next.value(), ef));
		}

		// @Id
		Id id = field.getAnnotation(Id.class);
		if (null != id) {
			// Check
			if (!ef.getMirror().isIntLike())
				throw error(entity, "@Id field [%s] must be a Integer!", field.getName());
			if (id.auto()) {
				ef.setType(FieldType.SERIAL);
				// 如果是自增字段，并且没有声明 '@Next' ，为其增加 SELECT MAX(id) ...
				if (null == field.getAnnotation(Next.class)) {
					ef.setAfterInsert(FieldQuerys.create("SELECT MAX($field) FROM $view", ef));
				}
			} else {
				ef.setType(FieldType.ID);
			}
		}

		// @Name
		Name name = field.getAnnotation(Name.class);
		if (null != name) {
			// Check
			if (!ef.getMirror().isStringLike())
				throw error(entity, "@Name field [%s] must be a String!", field.getName());
			// Not null
			ef.setNotNull(true);
			// Set Name
			if (name.casesensitive())
				ef.setType(FieldType.CASESENSITIVE_NAME);
			else
				ef.setType(FieldType.NAME);
		}

		// Prepare how to adapt the field value to PreparedStatement
		ef.setFieldAdapter(FieldAdapter.create(ef.getMirror(), ef.isEnumInt()));

		// Prepare how to adapt the field value from ResultSet
		ef.setValueAdapter(ValueAdapter.create(ef.getMirror(), ef.isEnumInt()));

		return ef;
	}

	private Link evalLink(DatabaseMeta db, Connection conn, Mirror<?> mirror, Field field) {
		try {
			// @One
			One one = field.getAnnotation(One.class);
			if (null != one) { // One > refer own field
				return new Link(mirror, field, one);
			}
			// @Many
			else { // Many > refer target field
				Many many = field.getAnnotation(Many.class);
				if (null != many) {
					return new Link(mirror, field, many);
				}
				// @ManyMany
				else {
					ManyMany mm = field.getAnnotation(ManyMany.class);
					if (null != mm) {
						// Read relation
						Statement stat = null;
						ResultSet rs = null;
						ResultSetMetaData rsmd = null;
						boolean fromName = false;
						boolean toName = false;
						try {
							stat = conn.createStatement();
							Segment tableName = new CharSegment(mm.relation());
							rs = stat.executeQuery(db.getResultSetMetaSql(TableName
									.render(tableName)));
							rsmd = rs.getMetaData();
							fromName = !Daos.isIntLikeColumn(rsmd, mm.from());
							toName = !Daos.isIntLikeColumn(rsmd, mm.to());
						} catch (Exception e) {
							if (log.isWarnEnabled())
								log.warnf("Fail to get table '%s', '%s' and '%s' "
										+ "will be taken as @Id ", mm.relation(), mm.from(), mm
										.to());
						} finally {
							Daos.safeClose(stat, rs);
						}
						return new Link(mirror, field, mm, fromName, toName);
					}
				}
			}
		} catch (Exception e) {
			throw Lang.makeThrow("Fail to eval linked field '%s' of class[%s] for the reason '%s'",
					field.getName(), mirror.getType().getName(), e.getMessage());
		}
		return null;
	}

	private boolean isPojoExistsColumnAnnField(Mirror<?> mirror) {
		for (Field f : mirror.getFields())
			if (null != f.getAnnotation(Column.class))
				return true;
		return false;
	}

	private EntityName evalEntityName(	Class<?> type,
										Class<? extends Annotation> annType,
										Class<? extends Annotation> dftAnnType) {
		Annotation ann = null;
		Class<?> me = type;
		while (null != me && !(me == Object.class)) {
			ann = me.getAnnotation(annType);
			if (ann != null) {
				String v = Mirror.me(annType).invoke(ann, "value").toString();
				if (!Strings.isBlank(v))
					return EntityName.create(v);
			}
			me = me.getSuperclass();
		}
		if (null != dftAnnType)
			return evalEntityName(type, dftAnnType, null);
		return EntityName.create(type.getSimpleName().toLowerCase());
	}
}
