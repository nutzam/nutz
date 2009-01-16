package com.zzh.dao.entity.analyzer;

import java.sql.Timestamp;
import java.util.Calendar;

import com.zzh.dao.entity.annotation.*;
import com.zzh.lang.meta.Email;


@Table("t_xyz")
public class TableEntity {

	@Column
	@Id
	private int id;

	@Column
	@PK
	private String name;

	@Column
	private short age;

	@Column("myEmail")
	@NotNull
	private Email email;

	@Column
	private Calendar calendar;

	@Column
	private java.sql.Date sqlDate;

	@Column
	private java.util.Date javaDate;

	@Column
	private Timestamp timestamp;

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

	public short getAge() {
		return age;
	}

	public void setAge(short age) {
		this.age = age;
	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public java.sql.Date getSqlDate() {
		return sqlDate;
	}

	public void setSqlDate(java.sql.Date sqlDate) {
		this.sqlDate = sqlDate;
	}

	public java.util.Date getJavaDate() {
		return javaDate;
	}

	public void setJavaDate(java.util.Date javaDate) {
		this.javaDate = javaDate;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}
