package com.zzh.trans;

import com.zzh.dao.entity.annotation.*;

@Table
public class Cat {
	@Column
	@Id
	private int id;

	@Column
	@Name
	private String name;

	@Column
	private int masterId;

	private Master master;

	public Master getMaster() {
		return master;
	}

	public void setMaster(Master master) {
		this.master = master;
	}

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

	public int getMasterId() {
		return masterId;
	}

	public void setMasterId(int masterId) {
		this.masterId = masterId;
	}

}
