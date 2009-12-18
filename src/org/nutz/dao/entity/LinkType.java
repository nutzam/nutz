package org.nutz.dao.entity;

/**
 * 映射类型
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public enum LinkType {
	/**
	 * 一对一映射
	 * 
	 * @see org.nutz.dao.entity.annotation.One
	 */
	One,
	/**
	 * 一对多映射
	 * 
	 * @see org.nutz.dao.entity.annotation.Many
	 */
	Many,
	/**
	 * 多对多映射
	 * 
	 * @see org.nutz.dao.entity.annotation.ManyMany
	 */
	ManyMany
}
