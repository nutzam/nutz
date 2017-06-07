package org.nutz.dao.nst.test;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("A")
public class A {
	@Id
	private int aid; 
	@Column("aname")
	private String aaa;
	public int getAid() {
		return aid;
	}
	public void setAid(int aid) {
		this.aid = aid;
	}
	public String getAaa() {
		return aaa;
	}
	public void setAaa(String aaa) {
		this.aaa = aaa;
	}
	@Override
	public String toString() {
		return "A [aid=" + aid + ", aaa=" + aaa + "]";
	}
}
