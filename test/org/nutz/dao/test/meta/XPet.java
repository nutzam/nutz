package org.nutz.dao.test.meta;

import java.sql.Timestamp;

import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.dao.interceptor.annotation.PrevUpdate;

@Table("t_xpet")
public class XPet {

    @Id
	private long id;
    @Name
    @PrevInsert(uu32 = true, nullEffective = true)
	private String name;
	
	@PrevInsert(now=true)
	private Timestamp createTime;
	@PrevUpdate(els=@EL("now()"))
	@PrevInsert(now=true)
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
