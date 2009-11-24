package org.nutz.dao.entity.next;

import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.annotation.Next;

public abstract class Nexts {

	/**
	 * 根据 '@Next' 注解以及数据库类型，为一个字段设置插入前后的二次查询。
	 * 
	 * @param meta
	 *            数据库元数据
	 * @param ef
	 *            实体字段
	 */
	public static void eval(DatabaseMeta meta, EntityField ef) {
		Next next = ef.getField().getAnnotation(Next.class);
	}

}
