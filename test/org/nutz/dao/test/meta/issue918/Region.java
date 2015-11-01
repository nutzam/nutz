package org.nutz.dao.test.meta.issue918;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;



/**
 * @author Young
 * 行政区域
 */
@Table("regions")
@Comment("行政区域")
public class Region extends AbstractTree<Region> {
 
	//private static final long serialVersionUID = 1L;
	
	@Column
	private String name;
	
	@Column
	@Comment("区域代码")
	private String code;
	
	@Column("zip_code")
	@Comment("邮政编码")
	private String zipCode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	
}