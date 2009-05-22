package com.zzh.ioc.meta;

import java.util.Map;

import com.zzh.dao.entity.annotation.*;
import com.zzh.json.Json;
import com.zzh.json.JsonFormat;
import com.zzh.json.ToJson;

@Table("nut_object")
@ToJson
public class Obj {

	public Obj() {
		singleton = true;
	}

	@Column
	@Id
	private int id;

	@Column
	@Name
	private String name;

	@Column
	private boolean singleton;

	@Column
	private String type;

	@Column("parent")
	private String parent;

	@Column("depmethod")
	private String deposeby;

	@Column("cmt")
	private String comment;

	@Column("deposer")
	private String deposer;

	@ManyMany(target = Val.class, relation = "nut_obj_args", from = "oid", to = "vid")
	private Val[] args;

	@Many(target = Fld.class, field = "objectId")
	private Fld[] fields;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Val[] getArgs() {
		return args;
	}

	public void setArgs(Val[] args) {
		this.args = args;
	}

	public Fld[] getFields() {
		return fields;
	}

	public void setFields(Fld[] fields) {
		this.fields = fields;
	}

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

	public boolean isSingleton() {
		return singleton;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parentName) {
		this.parent = parentName;
	}

	public String getDeposeby() {
		return deposeby;
	}

	public void setDeposeby(String deposeMethodName) {
		this.deposeby = deposeMethodName;
	}

	public String getDeposer() {
		return deposer;
	}

	public void setDeposer(String deposer) {
		this.deposer = deposer;
	}

	@Override
	public String toString() {
		return Json.toJson(this);
	}

	public String toJson(JsonFormat format) {
		Map<?, ?> map = Obj2Map.render(this);
		return Json.toJson(map, format);
	}
}

