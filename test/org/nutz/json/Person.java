package org.nutz.json;

import java.sql.Date;

public class Person {

	private int age;
	private String name;
	private String realname;
	private Date birthday;
	private Person father;
	private Company company;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Person getFather() {
		return father;
	}

	public void setFather(Person father) {
		this.father = father;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String dump() {
		return String.format("name\t:%s\nage\t:%d\nrealname:%s\nbirthday:%s", name, age, realname,
				birthday);
	}
}
