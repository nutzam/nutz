package com.zzh.ioc.db;

import java.util.HashMap;

import com.zzh.dao.entity.annotation.*;

@Table("nut_value")
public class ValueBean {

	@Column
	@Id
	private int id;

	@Column
	private String type;

	@Column
	private String value;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Object toValue() {
		if (type == null)
			return value;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(type, value);
		return map;
	}
}
