package com.zzh.dao.impl;

import com.zzh.dao.entity.annotation.*;

@Table("t_profile")
public class Profile {

	public Profile() {
		super();
	}

	/**
	 * @param id
	 * @param city
	 */
	public Profile(int id, String city) {
		this.id = id;
		this.city = city;
	}

	@Column
	@Id(IdType.STATIC)
	public int id;

	@Column
	public String city;

}
