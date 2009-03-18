package com.zzh.dao.impl;

import com.zzh.dao.entity.annotation.*;

@Table("mo_port")
public class Port {

	static Port make(int port) {
		Port p = new Port();
		p.value = port;
		return p;
	}

	@Column
	@Id
	public int id;

	@Column
	public int value;

}
