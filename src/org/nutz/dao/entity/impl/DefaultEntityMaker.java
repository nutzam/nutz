package org.nutz.dao.entity.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.entity.FieldValueType;
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
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.ValueType;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Readonly;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.View;
import org.nutz.dao.entity.born.Borns;
import org.nutz.dao.entity.next.NextQuery;
import org.nutz.dao.entity.next.Nexts;
import org.nutz.dao.entity.query.IntQuerys;
import org.nutz.dao.sql.FieldAdapter;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;

/**
 * This class must be drop after make() be dropped
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public class DefaultEntityMaker implements EntityMaker {

	private DatabaseMeta db;
	private Entity<Object> entity;

	public Entity<?> make(DatabaseMeta db, Class<?> type) {
		this.db = db;
		entity = new Entity<Object>();
		Mirror<?> mirror = Mirror.me(type);
		entity.setMirror(mirror);

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

		List<NextQuery> befores = new ArrayList<NextQuery>(5);
		List<NextQuery> afters = new ArrayList<NextQuery>(5);
		// For each fields ...
		for (Field f : mirror.getFields()) {
			// When the field declared @Many, @One, @ManyMany
			Link link = evalLink(mirror, f);
			if (null != link) {
				entity.addLinks(link);
			}
			// Then try to eval the field
			else {
				// Current POJO has @Column field, but current not, ignore it
				if (existsColumnAnnField && null == f.getAnnotation(Column.class))
					continue;
				// Create EntityField
				EntityField ef = evalField(f);

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

		// Then let's check the pks
		if (pkmap.size() > 0) {
			EntityField[] pks = new EntityField[pkmap.size()];
			for (int i = 0; i < pk.value().length; i++)
				pks[i] = pkmap.get(pk.value()[i]);

			entity.setPkFields(pks);
		}

		// Eval beforeInsert fields and afterInsert fields
		entity.setBefores(befores.toArray(new NextQuery[befores.size()]));
		entity.setAfters(afters.toArray(new NextQuery[afters.size()]));

		return entity;
	}

	private ErrorEntitySyntaxException error(Entity<?> entity, String fmt, Object... args) {
		return new ErrorEntitySyntaxException(String.format("[%s] : %s", null == entity ? "NULL"
				: entity.getType().getName(), String.format(fmt, args)));
	}

	private EntityField evalField(Field field) {
		Mirror<?> fieldType = Mirror.me(field.getType());
		field.setAccessible(true);
		EntityField ef = new EntityField(entity, field);
		// Eval field column name
		Column column = field.getAnnotation(Column.class);
		if (null == column || Strings.isBlank(column.value()))
			ef.setColumnName(field.getName());
		else
			ef.setColumnName(column.value());

		// @Readonly
		ef.setReadonly((field.getAnnotation(Readonly.class) != null));

		// @FieldType
		ValueType t = field.getAnnotation(ValueType.class);
		ef.setFieldType(null == t ? FieldValueType.AUTO : t.value());

		// @Default
		Default dft = field.getAnnotation(Default.class);
		if (null != dft)
			ef.setDefaultValue(new CharSegment(dft.value()));

		// @Next
		Nexts.eval(db, ef);

		// @Id
		Id id = field.getAnnotation(Id.class);
		if (null != id) {
			// Check
			if (!fieldType.isInteger())
				throw error(entity, "@Id field [%s] must be a Integer!", field.getName());
			if (id.auto())
				ef.setType(FieldType.SERIAL);
			else
				ef.setType(FieldType.ID);
			/*
			 * How to query next ID Just prepare the IntQuery object here,
			 * invoke it or not, upload the client programe
			 */
			ef.setSerialQuery(IntQuerys.serial(db, entity.getViewNameObject(), id.value(), ef
					.getColumnName()));
		}

		// @Name
		Name name = field.getAnnotation(Name.class);
		if (null != name) {
			// Check
			if (!fieldType.isStringLike())
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
		ef.setFieldAdapter(FieldAdapter.create(fieldType, ef.getFieldType()));

		// Prepare how to adapt the field value from ResultSet
		ef.setValueAdapter(ValueAdapter.create(fieldType, ef.getFieldType()));

		return ef;
	}

	private Link evalLink(Mirror<?> mirror, Field field) {
		try {
			One one = field.getAnnotation(One.class);
			if (null != one) { // One > refer own field
				return new Link(mirror, field, one);
			} else { // Many > refer target field
				Many many = field.getAnnotation(Many.class);
				if (null != many) {
					return new Link(mirror, field, many);
				} else {
					ManyMany mm = field.getAnnotation(ManyMany.class);
					if (null != mm) {
						return new Link(mirror, field, mm);
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
