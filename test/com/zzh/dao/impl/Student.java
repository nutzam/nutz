package com.zzh.dao.impl;

import java.util.Calendar;

import com.zzh.dao.entity.annotation.Column;
import com.zzh.dao.entity.annotation.Default;
import com.zzh.dao.entity.annotation.Id;
import com.zzh.dao.entity.annotation.Name;
import com.zzh.dao.entity.annotation.NotNull;
import com.zzh.dao.entity.annotation.Table;
import com.zzh.lang.Lang;
import com.zzh.lang.meta.Email;

@Table("student")
public class Student {

	@Column
	@Id
	private int id;

	@Column
	@NotNull
	@Name
	private String name;

	@Column
	private int age;

	@Column
	private Email email;

	@Column
	@Default("I am ${name}")
	private String aboutMe;

	@Column
	private Calendar birthday;

	@Column("isnew")
	private boolean isNew;

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
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

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public Calendar getBirthday() {
		return birthday;
	}

	public void setBirthday(Calendar birthday) {
		this.birthday = birthday;
	}

	public boolean equals(Student stu) {
		if (id != stu.id)
			return false;
		if (!Lang.equals(name, stu.name))
			return false;
		if (!Lang.equals(aboutMe, stu.aboutMe))
			return false;
		if (age != stu.age)
			return false;
		if (null == email && stu.email != null)
			return false;
		if (!email.equals(stu.email))
			return false;
		if (null == birthday && null == stu.birthday)
			return true;
		if (null == birthday || null == stu.birthday)
			return false;
		if (birthday.getTimeInMillis() != stu.birthday.getTimeInMillis())
			return false;
		return true;
	}

}
