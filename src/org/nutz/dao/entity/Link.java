package org.nutz.dao.entity;

import java.lang.reflect.Field;

import org.nutz.dao.TableName; //import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;

/**
 * 描述了一条映射，它可以是：
 * <ul>
 * <li>一对一
 * <li>一对多
 * <li>多对多
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Bird.Wyatt(bird.wyatt@gmail.com)
 * 
 */
public class Link {

	/**
	 * 创建一条一对一的映射，将<b>本 POJO</b> 的一个字段通过本身的一个参考字段指向<b>目标POJO</b>的主键。
	 * <p>
	 * <b style=color:red>这里需要注意的是：</b><br>
	 * 你的 <b>本 POJO</b>的参考字段类型要同<b>目标POJO</b>的主键要对应。即，
	 * <ul>
	 * <li><b>目标POJO</b>不可以是复合主键
	 * <li>如果你的 <b>本 POJO</b>参考字段类型是整数，<b>目标POJO</b>的主键就必须是整数型主键。
	 * <li>如果你的 <b>本 POJO</b>参考字段类型是字符串，<b>目标POJO</b>的主键就必须是字符型主键
	 * </ul>
	 * 
	 * 
	 * @param mirror
	 *            <b>本 POJO</b>的类型
	 * @param field
	 *            被映射的字段，它的类型应该是<b>目标POJO</b>的类型的接口或者超类
	 * @param targetClass
	 *            <b>目标POJO</b>的类型
	 * @param referFld
	 *            <b>本 POJO</b>的参考字段
	 * @param targetPkFld
	 *            <b>目标POJO</b>的主键
	 * @return 映射对象
	 */
	public static Link getLinkForOne(	Mirror<?> mirror,
										Field field,
										Class<?> targetClass,
										Field referFld,
										Field targetPkFld) {
		Link link = new Link(field, targetClass);
		link.type = LinkType.One;
		link.referField = referFld;
		link.targetField = targetPkFld;
		return link;

	}

	/**
	 * 创建一条一对多的映射，将<b>本 POJO</b> 的一个字段通过<b>目标POJO</b>一个参考字段指向本身的主键。 因此<b>本
	 * POJO</b>的这个映射字段可以是一个集合或数组，因为可能有多个<b>目标POJO</b>指向自己
	 * <p>
	 * <b style=color:red>这里需要注意的是：</b><br>
	 * 你的 <b>目标POJO</b>的参考字段类型要同<b>本POJO</b>的主键要对应。即，
	 * <ul>
	 * <li><b>本POJO</b>不可以是复合主键
	 * <li>如果你的 <b>目标 POJO</b>参考字段类型是整数，<b>本POJO</b>的主键就必须是整数型主键。
	 * <li>如果你的 <b>目标 POJO</b>参考字段类型是字符串，<b>本POJO</b>的主键就必须是字符型主键
	 * <li>如果 referField 为 null，则将映射 <b>目标 POJO</b>的全部集合 -- 比较适用于枚举类型。
	 * </ul>
	 * 
	 * 
	 * @param mirror
	 *            <b>本 POJO</b>的类型
	 * @param field
	 *            被映射的字段，它的类型应该是<b>目标POJO</b>的类型的接口或者超类
	 * @param targetClass
	 *            <b>目标POJO</b>的类型
	 * @param targetReferFld
	 *            <b>本 POJO</b>主键
	 * @param pkFld
	 *            <b>目标POJO</b>的参考字段
	 * @param key
	 *            如果被映射字段是个 Map， 你需要声明一下，<b>目标POJO</b>哪个字段是用来作为键值的。 默认的，赋值 null
	 *            即可
	 * @return 映射对象
	 */
	public static Link getLinkForMany(	Mirror<?> mirror,
										Field field,
										Class<?> targetClass,
										Field targetReferFld,
										Field pkFld,
										String key) {
		Link link = new Link(field, targetClass);
		link.type = LinkType.Many;
		link.mapKeyField = Strings.isBlank(key) ? null : key;
		link.targetField = targetReferFld;
		link.referField = pkFld;
		return link;

	}

	/**
	 * 创建一条多对多的映射，通过一个关联表的两个字段
	 * <ul>
	 * <li>from - selfClass.PK
	 * <li>to - targetClass.PK
	 * </ul>
	 * 为当前字段关联一组<b>目标POJO</b>
	 * <p>
	 * <b style=color:red>这里需要注意的是：</b><br>
	 * 无论是 from 和 to，都要和相对应的对象的主键类型一直
	 * <ul>
	 * <li><b>本POJO</b>和<b>目标POJO</b>都不可以是复合主键
	 * <li>如果 from 字段类型是整数，<b>本POJO</b>的主键就必须是整数型主键。
	 * <li>如果 to 字段类型是字符串，<b>本POJO</b>的主键就必须是字符型主键
	 * </ul>
	 * 
	 * 
	 * @param mirror
	 *            <b>本 POJO</b>的类型
	 * @param field
	 *            被映射的字段，它的类型应该是<b>目标POJO</b>的类型的接口或者超类
	 * @param targetClass
	 *            <b>目标POJO</b>的类型
	 * @param selfPk
	 *            <b>本 POJO</b>的主键
	 * @param targetPk
	 *            <b>目标 POJO</b>的主键
	 * @param key
	 *            如果被映射字段是个 Map， 你需要声明一下，<b>目标POJO</b>哪个字段是用来作为键值的。 <br>
	 *            默认的，赋值 null 即可
	 * @param relation
	 *            关联表名
	 * @param from
	 *            在关联表中指向 <b>本 POJO</b>主键的字段名
	 * @param to
	 *            在关联表中指向 <b>目标POJO</b>主键的字段名
	 * @return 映射对象
	 */
	public static Link getLinkForManyMany(	Mirror<?> mirror,
											Field field,
											Class<?> targetClass,
											Field selfPk,
											Field targetPk,
											String key,
											String relation,
											String from,
											String to) {
		Link link = new Link(field, targetClass);
		link.type = LinkType.ManyMany;
		link.mapKeyField = "".equals(key) ? null : key;
		link.from = from;
		link.to = to;
		link.relation = Relation.make(relation);
		link.referField = selfPk;
		link.targetField = targetPk;
		if (null == link.referField || null == link.targetField) {
			throw Lang.makeThrow("Fail to make ManyMany link for [%s].[%s], target: [%s]."
					+ "\n referField: [%s]" + "\n targetField: [%s]", mirror.getType().getName(),
					field.getName(), targetClass.getName(), link.referField, link.targetField);
		}
		return link;

	}

	private Link(Field field, Class<?> targetClass) {
		this.ownField = field;
		this.targetClass = targetClass;
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

	/**
	 * 目标POJO类型
	 */
	private Class<?> targetClass;
	/**
	 * 目标字段。即<b>"被"</b>指向字段。
	 * <ul>
	 * <li>'@One' - 它就是 targetClass.PK
	 * <li>'@Many' - 它就是 targetClass.fld
	 * <li>'@ManyMany' - 它就是 targetClass.PK
	 * </ul>
	 */
	private Field targetField;
	/**
	 *参考字段。即<b>"指向"</b>目标字段的字段
	 *<ul>
	 *<li>'@One' - 它就是 selfClass.fld
	 * <li>'@Many' - 它就是 selfClass.PK
	 * <li>'@ManyMany' - 它就是 selfClass.PK。这里有点特殊，它也是被 relation 指向的
	 *</ul>
	 */
	private Field referField;
	/**
	 * 映射字段。即在这个字段上声明的映射关系
	 */
	private Field ownField;
	/**
	 * 映射的类型
	 * 
	 * @see org.nutz.dao.entity.LinkType
	 */
	private LinkType type;
	/**
	 * 仅 '@ManyMany'，记录了中间表全名。支持动态表名。
	 * <p>
	 * 它有可能是 String 类型或者 Relation 类型。 如果是 Relation 类型则表示动态表名
	 */
	private Object relation;
	/**
	 * 仅 '@ManyMany'，记录了中间表指向 selfClass.PK 的字段
	 */
	private String from;
	/**
	 * 仅 '@ManyMany'，记录了中间表指向 targetClass.PK 的字段
	 */
	private String to;
	/**
	 *如果被映射字段是个 Map， 你需要声明一下，<b>目标POJO</b>哪个字段是用来作为键值的。 默认的，赋值 null 即可
	 */
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
