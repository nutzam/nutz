package com.zzh.ioc.db;

import java.util.List;

import com.zzh.dao.entity.annotation.*;

@Table("nut_object")
public class ObjectBean {

	public ObjectBean() {
		singleton = true;
	}

	@Column
	@Id
	private int id;

	@Column
	@Name
	private String name;

	@Column
	private boolean singleton;

	@Column
	private boolean anonymous;

	@Column
	private Class<?> type;

	@ManyMany(target = ValueBean.class, relation = "nut_obj_args", from = "oid", to = "vid")
	private List<ValueBean> args;

	@Many(target = FieldBean.class, field = "objectId")
	private List<FieldBean> fields;

	public List<ValueBean> getArgs() {
		return args;
	}

	public void setArgs(List<ValueBean> args) {
		this.args = args;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public List<FieldBean> getFields() {
		return fields;
	}

	public void setFields(List<FieldBean> fields) {
		this.fields = fields;
	}

}
