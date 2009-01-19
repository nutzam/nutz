package com.zzh.trans;

import com.zzh.dao.entity.annotation.*;

@Table
public class Company {
	@Column
	@Id
	private int id;

	@Column
	@Name
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
