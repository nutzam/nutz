package com.zzh.ioc.db;

import java.util.List;

import com.zzh.dao.entity.annotation.*;
import com.zzh.lang.Mirror;

@Table("nut_object")
public class ObjectBean {

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
	private Mirror<?> type;

	@Many(target = FieldBean.class, field = "objectId")
	private List<FieldBean> fields;

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

	public Mirror<?> getType() {
		return type;
	}

	public void setType(Mirror<?> type) {
		this.type = type;
	}

	public List<FieldBean> getFields() {
		return fields;
	}

	public void setFields(List<FieldBean> fields) {
		this.fields = fields;
	}

}
