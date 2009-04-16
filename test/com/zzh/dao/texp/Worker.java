package com.zzh.dao.texp;

import com.zzh.dao.entity.annotation.*;

@Table("t_worker")
public class Worker {

	@Column("wid")
	@Id
	public int id;

	@Column("wname")
	@Name
	public String name;

	@Column("ct")
	public String city;

	@Column
	public short age;

	@Column("days")
	public int workingDay;

}
