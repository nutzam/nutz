package com.zzh.dao;

import java.sql.Timestamp;

import com.zzh.dao.entity.annotation.*;

@Table("t_one")
public class One {

	public static One make(int i) {
		One o = new One();
		o.txt = "one_" + i;
		o.modified = new Timestamp(System.currentTimeMillis());
		return o;
	}

	@Column
	@Id
	private int id;

	@Column
	private String txt;

	@Column
	private Timestamp modified;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}

	public Timestamp getModified() {
		return modified;
	}

	public void setModified(Timestamp modified) {
		this.modified = modified;
	}

}
