package com.zzh.dao.impl;

import com.zzh.dao.entity.annotation.*;

@Table("mo_other")
public class Other {

	static Other make(String text) {
		Other other = new Other();
		other.text = text;
		return other;
	}

	@Column("hid")
	public int hostId;

	@Column
	@Name
	public String text;
}
