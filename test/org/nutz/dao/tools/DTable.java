package org.nutz.dao.tools;

import java.util.LinkedList;
import java.util.List;

public class DTable {

	private String name;

	private List<DField> fields;

	public DTable() {
		fields = new LinkedList<DField>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DTable addField(DField field) {
		fields.add(field);
		return this;
	}
}
