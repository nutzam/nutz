package com.zzh.dao.entity;

import java.lang.reflect.Field;

import com.zzh.dao.TableName;
import com.zzh.dao.entity.annotation.*;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.segment.CharSegment;

public class Link {

	static Link eval(Mirror<?> mirror, Field field) {
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
			throw Lang.makeThrow("Fail to eval linked field '%s' for the reason '%s'", field
					.getName(), e.getMessage());
		}
		return null;
	}

	private Link(Mirror<?> mirror, Field field, One one) throws NoSuchFieldException {
		this.ownField = field;
		this.type = LinkType.One;
		this.targetClass = one.target();
		evalDynamicTable();
		if (this.isDynamicTarget()) {
			throw Lang.makeThrow("Dynamic Target [%s] only support by @Many or @ManyMany",
					this.targetClass.getSimpleName());
		}
		this.referField = mirror.getField(one.field());
		if (Mirror.me(this.referField.getType()).isStringLike()) {
			this.targetField = Mirror.me(this.targetClass).getField(Name.class);
		} else {
			this.targetField = Mirror.me(this.targetClass).getField(Id.class);
		}
	}

	private Link(Mirror<?> mirror, Field field, Many many) throws NoSuchFieldException {
		this.ownField = field;
		this.type = LinkType.Many;
		this.mapKeyField = Lang.NULL.equals(many.mapKeyField()) ? null : many.mapKeyField();
		this.targetClass = many.target();
		evalDynamicTable();
		if (this.isDynamicTarget()) {
			this.referField = mirror.getField(many.field());
			this.targetField = null;
		} else {
			this.targetField = Mirror.me(this.targetClass).getField(many.field());
			if (Mirror.me(this.targetField.getType()).isStringLike()) {
				this.referField = mirror.getField(Name.class);
			} else {
				this.referField = mirror.getField(Id.class);
			}
		}
	}

	private Link(Mirror<?> mirror, Field field, ManyMany mm) {
		this.ownField = field;
		this.type = LinkType.ManyMany;
		this.mapKeyField = Lang.NULL.equals(mm.mapKeyField()) ? null : mm.mapKeyField();
		this.targetClass = mm.target();
		evalDynamicTable();
		this.from = mm.from();
		this.to = mm.to();
		this.relation = Relation.make(mm.relation());
		// looking for the target Id or Name field
		Mirror<?> ta = Mirror.me(targetClass);
		for (Field f : ta.getFields()) {
			this.targetField = f;
			if (f.getAnnotation(Id.class) != null) {
				Field nameField = null;
				for (Field ff : mirror.getFields()) {
					if (ff.getAnnotation(Id.class) != null) {
						this.referField = ff;
						break;
					} else if (ff.getAnnotation(Name.class) != null) {
						nameField = ff;
					}
				}
				if (this.referField == null && nameField != null) {
					this.referField = nameField;
				}
				if (null != this.referField)
					break;
			} else if (f.getAnnotation(Name.class) != null) {
				Field idField = null;
				for (Field ff : mirror.getFields()) {
					if (ff.getAnnotation(Name.class) != null) {
						this.referField = ff;
						break;
					} else if (ff.getAnnotation(Id.class) != null) {
						idField = ff;
					}
				}
				if (this.referField == null && idField != null) {
					this.referField = idField;
				}
				if (null != this.referField)
					break;
			}
		}
		if (null == this.referField)
			throw Lang.makeThrow("Fail to make ManyMany link for [%s]->[%s], target: [%s]", mirror
					.getType().getName(), field.getName(), targetClass.getName());

	}

	private void evalDynamicTable() {
		Table annTab = this.targetClass.getAnnotation(Table.class);
		if (null != annTab && (new CharSegment(annTab.value())).keys().size() > 0)
			this.targetDynamic = true;
	}

	private Class<?> targetClass;
	private Field targetField;
	private Field referField;
	private Field ownField;
	private LinkType type;
	private Object relation;
	private String from;
	private String to;
	private String mapKeyField;
	private boolean targetDynamic;

	public boolean isDynamicTarget() {
		return targetDynamic;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public String getMapKeyField() {
		return mapKeyField;
	}

	public Field getTargetField() {
		return targetField;
	}

	public Field getReferField() {
		return referField;
	}

	public boolean isMany() {
		return type == LinkType.Many;
	}

	public boolean isOne() {
		return type == LinkType.One;
	}

	public boolean isManyMany() {
		return type == LinkType.ManyMany;
	}

	public Field getOwnField() {
		return ownField;
	}

	public static class Relation {

		static Object make(String s) {
			if (null == s)
				throw Lang.makeThrow("In @ManyMany, relation is required!");
			CharSegment cs = new CharSegment(s);
			if (cs.keys().size() == 0)
				return s;
			Relation r = new Relation();
			r.cs = cs;
			return r;
		}

		CharSegment cs;

		@Override
		public String toString() {
			return TableName.getName(cs);
		}

	}

	public String getRelation() {
		return relation.toString();
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

}
