package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.*;

@Table("t_stabber")
public class Stabber {

	@Column
	@Id
	private int id;

	@Column
	@Name
	private String name;

	@Column("cnum")
	@Next("SELECT MAX(v) FROM t_stabber_seq")
	private int caseNumber;

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

	public int getCaseNumber() {
		return caseNumber;
	}

	public void setCaseNumber(int caseNumber) {
		this.caseNumber = caseNumber;
	}

}
