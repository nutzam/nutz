package com.zzh.dao.impl;

import com.zzh.dao.entity.annotation.*;

@Table("mo_host")
public class Host {

	static Host make(String name) {
		Host host = new Host();
		host.name = name;
		return host;
	}

	@Column
	@Id
	public int id;

	@Column
	@Name
	public String name;

	@Many(target = Other.class, field = "hostId")
	public Other[] others;

	@Column("tid")
	public int typeId;
	
	@One(target = HostType.class, field = "typeId")
	public HostType type;
}
