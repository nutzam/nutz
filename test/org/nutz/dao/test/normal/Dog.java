package org.nutz.dao.test.normal;

import org.nutz.dao.entity.annotation.*;

@Table("t_dog")
public class Dog {

	@Column("mid")
	private int masterId;

	@Column
	private int id;

	@Column
	private String name;

	@Column
	private int age;

	public int getMasterId() {
		return masterId;
	}

	public void setMasterId(int masterId) {
		this.masterId = masterId;
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

}
