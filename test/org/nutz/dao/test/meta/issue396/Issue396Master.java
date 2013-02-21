package org.nutz.dao.test.meta.issue396;

import java.util.List;

import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.SQL;
import org.nutz.dao.entity.annotation.Table;

@Table("x_master")
public class Issue396Master {

	@Id(auto=false)
	@Prev({@SQL(value="select ${table}.currval from dual", db=DB.ORACLE),
			@SQL(value="select max(id)+1 from ${table}")})
	private int id;
	
	@Name
	private String name;
	
	@Many(field = "id", target = Issue396Master.class)
	private List<Issue396Pet> pets;

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

	public List<Issue396Pet> getPets() {
		return pets;
	}

	public void setPets(List<Issue396Pet> pets) {
		this.pets = pets;
	}
	
	
}
