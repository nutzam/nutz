package org.nutz.dao.tools.impl.meta;

import java.sql.Timestamp;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_one_table")
public class OnePojo {

	@Id
	private int id;
	@Column
	private String name;
	@Column
	private long xId;
	@Column
	private boolean isNew;
	@Column
	private Timestamp timestamp;
	
	@Column("cversion")
	private long version;

	//=========Getter/Setter
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

	public long getxId() {
		return xId;
	}

	public void setxId(long xId) {
		this.xId = xId;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}
