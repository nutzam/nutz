package com.zzh.ioc.db;

import com.zzh.dao.entity.annotation.*;

@Table("nut_field")
public class FieldBean {

	@Column
	private int objectId;

	@Column
	private String name;

	@Column
	private String value;

	public int getObjectId() {
		return objectId;
	}

	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
