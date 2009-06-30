package org.nutz.ioc.meta;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

@Table("nut_field")
public class Fld {

	@Column("oid")
	private int objectId;

	@Column
	private String name;

	@Column("vid")
	private int valueId;

	@One(target = Val.class, field = "valueId")
	private Val val;

	public Val getVal() {
		return val;
	}

	public void setVal(Val value) {
		this.val = value;
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

