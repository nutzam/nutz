package org.nutz.dao.test.meta.issue396;

import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.SQL;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.test.meta.Master;

@Table("x_pet")
public class Issue396Pet {

	@Id(auto=false)
	@Prev({@SQL(value="select ${table}.currval from dual", db=DB.ORACLE),
		@SQL(value="select max(id)+1 from ${table}")})
	private int id;
	
	@Name
	private String name;
	
	@Column
	private int masterId;
	
	@One(field = "masterId", target = Master.class)
	private Issue396Master master;

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

	public int getMasterId() {
		return masterId;
	}

	public void setMasterId(int masterId) {
		this.masterId = masterId;
	}

	public Issue396Master getMaster() {
		return master;
	}

	public void setMaster(Issue396Master master) {
		this.master = master;
	}
	
	
}
