package org.nutz.dao.entity;

/**
 * 字段类型。
 * <p>
 * 本枚举值不包括的类型，由 Dao 自行根据 Java 的字段类型来判断
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public enum FieldType {

	/**
	 * 静态整数主键 (@Id(auto=false))
	 * 
	 * @see org.nutz.dao.entity.annotation.Id
	 */
	ID,

	/**
	 * 自增整数主键 - 默认 (@Id)
	 * 
	 * @see org.nutz.dao.entity.annotation.Id
	 */
	SERIAL,

	/**
	 * 字符主键，大小写不敏感 (@Name(casesensitive=false))
	 * 
	 * @see org.nutz.dao.entity.annotation.Name
	 */
	NAME,
	/**
	 * 大小写敏感的字符主键 - 默认 (@Name)
	 * 
	 * @see org.nutz.dao.entity.annotation.Name
	 */
	CASESENSITIVE_NAME,
	/**
	 * 主键（复合主键之一）
	 * 
	 * @see org.nutz.dao.entity.annotation.PK
	 */
	PK,
	/**
	 * 枚举类型，在数据库中是整数
	 */
	ENUM_INT

}
