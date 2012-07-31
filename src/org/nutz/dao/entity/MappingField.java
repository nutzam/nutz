package org.nutz.dao.entity;

import java.sql.ResultSet;

import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.jdbc.ValueAdaptor;

/**
 * 这个接口描述了一个数据库字段与Java字段的映射关系
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface MappingField extends EntityField {

	/**
	 * 通过 Record 为映射字段注入值
	 * 
	 * @param obj
	 *            被注入对象
	 * @param rec
	 *            结果集
	 */
	void injectValue(Object obj, Record rec);

	/**
	 * 通过 resultSet 为映射字段注入值
	 * 
	 * @param obj
	 *            被注入对象
	 * @param rs
	 *            结果集
	 */
	void injectValue(Object obj, ResultSet rs);

	/**
	 * @return 字段值适配器
	 */
	ValueAdaptor getAdaptor();

	/**
	 * 设置字段值适配器
	 * 
	 * @param adaptor
	 *            字段值适配器
	 */
	void setAdaptor(ValueAdaptor adaptor);

	/**
	 * @return 数据库中的字段名
	 */
	String getColumnName();

	/**
	 * @return 数据库中字段的注释
	 */
	String getColumnComment();

	/**
	 * @return 数据库中的字段类型
	 */
	ColType getColumnType();

	/**
	 * 设置字段在数据库中的类型
	 * 
	 * @param colType
	 *            数据库字段的类型
	 */
	void setColumnType(ColType colType);

	/**
	 * 根据实体的实例对象，获取默认值
	 * 
	 * @param obj
	 *            当前实体的实例对象
	 * 
	 * @return 数据库字段的默认值
	 * 
	 * @see org.nutz.dao.entity.annotation.Default
	 */
	String getDefaultValue(Object obj);

	/**
	 * @return 字段宽度。默认 0 表示自动决定
	 */
	int getWidth();

	/**
	 * @return 字段的精度，仅浮点有效。默认 2
	 */
	int getPrecision();

	/**
	 * @return 当前字段是否是主键（包括复合主键）
	 */
	boolean isPk();

	/**
	 * @return 当前字段是否是复合主键
	 */
	boolean isCompositePk();

	/**
	 * @return 当前字段是否是数字型主键
	 */
	boolean isId();

	/**
	 * @return 当前字段是否是字符型主键
	 */
	boolean isName();

	/**
	 * @return 当前字段是否是只读
	 */
	boolean isReadonly();

	/**
	 * 将字段设置成只读
	 */
	void setAsReadonly();

	/**
	 * @return 字段是否设置了默认值
	 */
	boolean hasDefaultValue();

	/**
	 * @return 当前字段有非空约束
	 */
	boolean isNotNull();

	/**
	 * @return 是否为无符号
	 */
	boolean isUnsigned();

	/**
	 * @return 当前字段是否大小写敏感
	 */
	boolean isCasesensitive();

	/**
	 * 将字段设置成非空约束
	 */
	void setAsNotNull();

	/**
	 * 这个判断仅仅对于创建语句有作用。
	 * 
	 * @return 当前字段是否是自增的
	 */
	boolean isAutoIncreasement();

	/**
	 * @return 当前字段是否有注释。
	 */
	boolean hasColumnComment();

	void setCustomDbType(String customDbType);

	String getCustomDbType();

	/**
	 * @return 当前字段是否参与保存操作
	 */
	boolean isInsert();

	/**
	 * @return 当前字段是否参与更新操作
	 */
	boolean isUpdate();

}
