package com.zzh.dao.impl;

import com.zzh.dao.entity.annotation.*;

@Table("mo_address")
public class Address {

	static Address make(String IP, String comment) {
		Address adr = new Address();
		adr.ip = IP;
		adr.comment = comment;
		return adr;
	}

	@Column
	@Name
	public String ip;

	@Column("cmt")
	public String comment;
}
