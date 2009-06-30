package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.*;

@Table("dao_country")
public class Country {

	public static Country make(String name){
		Country c = new Country();
		c.setName(name);
		return c;
	}

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
