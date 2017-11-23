package org.nutz.dao.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.Nesting;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.SqlType;
import org.nutz.dao.util.Pojos;
import org.nutz.lang.Lang;

public class SimpleNesting implements Nesting {

	private static final String DISTINCT = "distinct";

	public SimpleNesting(Dao dao) {
		this.expert = dao.getJdbcExpert();
		holder = dao.getEntityHolder();
	}

	protected JdbcExpert expert;
	protected EntityHolder holder;
	private Pojo pojo;

	@Override
	public Nesting select(String names, Class<?> clazz, Condition cnd) {
		make(clazz);
		distinct(pojo, names);
		pojo.append(Pojos.Items.queryEntityFields());
		pojo.append(Pojos.Items.wrap("FROM"));
		pojo.append(Pojos.Items.entityViewName());
		pojo.append(Pojos.Items.cnd(cnd));
		if (cnd instanceof Cnd) {
			Pager pager = ((Cnd) cnd).getPager();
			if (pager != null) {
				pojo.setPager(pager);
			}
		}
		expert.formatQuery(pojo);
		return this;
	}

	private Pojo make(Class<?> clazz) {
		Entity<?> en = holder.getEntity(clazz);
		pojo = Pojos.pojo(expert, en, SqlType.SELECT);
		pojo.setEntity(en);
		return pojo;
	}

	private Pojo distinct(Pojo pojo, String names) {
		if (!Lang.isEmpty(names) && names.length() != 0) {
			List<String> nameList = Arrays.asList(names.trim().split(","));
			if (names.toLowerCase().contains(DISTINCT)) {
				pojo.append(Pojos.Items.wrap(DISTINCT));
				// distinct只能作用一个在一个字段上
				// 做一个排序处理,把DISTINCT的字段移到第一个
				for (int i = 0; i < nameList.size(); ++i) {
					if (nameList.get(i).toLowerCase().contains(DISTINCT)) {
						Collections.swap(nameList, 0, i);
						// 为了让字段作为正则匹配到列,删除DINSTINCT
						nameList.set(0, nameList.get(0).toLowerCase().replace(DISTINCT, "").toLowerCase());
						break;
					}
				}
			}
			StringBuilder sb = new StringBuilder();
			for (String name : nameList) {
				sb.append(name.trim());
				sb.append("|");
			}
			sb.setLength(sb.length() - 1);
			pojo.getContext().setFieldMatcher(FieldMatcher.make(sb.toString(), null, true));
		}
		return pojo;
	}

	@Override
	public String toString() {
		return pojo.toString();
	}
}
