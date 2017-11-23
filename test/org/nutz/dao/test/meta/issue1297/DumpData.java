package org.nutz.dao.test.meta.issue1297;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("t_test_dump_data")
public class DumpData {

	@Name
	private String id;

	@Column(hump = true)
	private String resId;

	@Column(hump = true)
	private long pushTime;

	@Column
	@Comment("打开推送")
	private long converted;

	@Column
	@Comment("收到推送")
	private long successful;

	@Column
	private String title;

	@Column("is_modify")
	private boolean modify;

	@Column
	private String category;

	@Column
	private String area;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public long getPushTime() {
		return pushTime;
	}

	public void setPushTime(long pushTime) {
		this.pushTime = pushTime;
	}

	public long getConverted() {
		return converted;
	}

	public void setConverted(long converted) {
		this.converted = converted;
	}

	public long getSuccessful() {
		return successful;
	}

	public void setSuccessful(long successful) {
		this.successful = successful;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isModify() {
		return modify;
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

}