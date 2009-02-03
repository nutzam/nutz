package com.zzh.dao.impl;

import com.zzh.dao.entity.annotation.*;

@Table("mo_host_type")
public class HostType {

	static HostType make(int id, String text) {
		HostType type = new HostType();
		type.id = id;
		type.text = text;
		return type;
	}

	@Column
	@Id(IdType.STATIC)
	public int id;

	@Column
	public String text;
}
