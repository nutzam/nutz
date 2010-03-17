package org.nutz.dao.entity;

import java.lang.reflect.Field;

import org.nutz.dao.TableName;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.segment.CharSegment;

/**
 * @author zozoh(zozohtnt@gmail.com)
 * @author Bird.Wyatt(bird.wyatt@gmail.com)
 * 
 */
public class Link {

	public static Link getLinkForOne(Field field, Class<?> targetClass,
			Field referField, Field targetField) throws NoSuchFieldException {
		Link link = new Link(field, targetClass, LinkType.One, referField,
				targetField);
		return link;
	}

	private Link(Field field, Class<?> targetClass, LinkType type,
			Field referField, Field targetField) throws NoSuchFieldException {
		this.ownField = field;
		this.targetClass = targetClass;
		this.type = type;
		this.referField = referField;
		this.targetField = targetField;
	}

	public static Link getLinkForMany(Field field, Class<?> targetClass,
			Field referField, Field targetField, String key)
			throws NoSuchFieldException {
		Link link = new Link(field, targetClass, LinkType.Many, referField,
				targetField);
		link.mapKeyField = "".equals(key) ? null : key;
		return link;

	}

	public static Link getLinkForManyMany(Mirror<?> mirror, Field field,
			Class<?> targetClass, String key, String from, String to,
			String relation, Field referField, Field targetField)
			throws NoSuchFieldException {
		Link link = new Link(field, targetClass, LinkType.ManyMany, referField,
				targetField);
		link.mapKeyField = "".equals(key) ? null : key;
		link.from = from;
		link.to = to;
		link.relation = Relation.make(relation);

		if (null == link.referField || null == link.targetField) {
			throw Lang.makeThrow(
					"Fail to make ManyMany link for [%s].[%s], target: [%s]."
							+ "\n referField: [%s]" + "\n targetField: [%s]",
					mirror.getType().getName(), field.getName(), targetClass
							.getName(), link.referField, link.targetField);
		}
		return link;

	}

	// private void evalMore(String dynamicBy) {
	// Cascade cc =
	// this.targetClass.getAnnotation(Cascade.class);
	// if (null != cc && cc.value() == Cascade.TYPE.ON)
	// cascade = true;
	// Table annTab =
	// this.targetClass.getAnnotation(Table.class);
	// if (null != annTab && (new
	// CharSegment(annTab.value())).keys().size() > 0)
	// this.targetDynamic = true;
	// dynamicReferField =
	// DynamicReferPicker.eval(targetClass, dynamicBy);
	// }

	private Class<?> targetClass;
	private Field targetField;
	private Field referField;
	private Field ownField;
	private LinkType type;
	private Object relation;
	private String from;
	private String to;
	private String mapKeyField;

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
			return TableName.render(cs);
		}

		public String getOrginalString() {
			return cs.getOrginalString();
		}

	}

	public String getRelation() {
		return relation.toString();
	}

	public String getRelationOrignalString() {
		if (relation instanceof Relation)
			return ((Relation) relation).getOrginalString();
		return relation.toString();
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

}
