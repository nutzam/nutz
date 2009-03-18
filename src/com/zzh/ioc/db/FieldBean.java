package com.zzh.ioc.db;

import com.zzh.dao.entity.annotation.*;

@Table("nut_field")
public class FieldBean {

	@Column("oid")
	private int objectId;

	@Column
	private String name;

	@Column("vid")
	private int valueId;

	@One(target = ValueBean.class, field = "valueId")
	private ValueBean value;

	public ValueBean getValue() {
		return value;
	}

	public void setValue(ValueBean value) {
		this.value = value;
	}

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

	public int getValueId() {
		return valueId;
	}

	public void setValueId(int valueId) {
		this.valueId = valueId;
	}

}
