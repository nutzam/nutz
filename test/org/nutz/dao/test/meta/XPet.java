package org.nutz.dao.test.meta;

import java.sql.Timestamp;

import org.nutz.dao.entity.annotation.Table;

@Table("t_xpet")
public class XPet {

	private long id;
	private String name;
	private Timestamp createTime;
	private Timestamp updateTime;
	private Timestamp otherTime;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public Timestamp getOtherTime() {
		return otherTime;
	}
	public void setOtherTime(Timestamp otherTime) {
		this.otherTime = otherTime;
	}
	
	
}
