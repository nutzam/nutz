package org.nutz.dao.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DTable {

	private String name;

	private List<DField> pks;

	private List<DField> ais;

	private List<DField> fields;

	private Map<String, DField> maps;

	public DTable() {
		fields = new LinkedList<DField>();
		pks = new LinkedList<DField>();
		ais = new LinkedList<DField>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DTable addField(DField field) {
		fields.add(field);
		if (field.isAutoIncreament())
			ais.add(field);
		if (field.isPrimaryKey())
			pks.add(field);
		return this;
	}

	public List<DField> getPks() {
		return pks;
	}

	public String getPkNames() {
		if (pks.isEmpty())
			return null;
		Iterator<DField> it = pks.iterator();
		String names = it.next().getName();
		while (it.hasNext())
			names += "," + it.next().getName();
		return names;
	}

	public String getPkNames(char wrapper) {
		if (pks.isEmpty())
			return null;
		if (wrapper < 32)
			return getPkNames();
		
		Iterator<DField> it = pks.iterator();
		String names = it.next().getName();
		while (it.hasNext())
			names += "," + wrapper + it.next().getName() + wrapper;
		return names;
	}

	public List<DField> getAutoIncreaments() {
		return ais;
	}

	public List<DField> getFields() {
		return fields;
	}

	public DField getField(String name) {
		if (null == maps) {
			synchronized (this) {
				if (null == maps) {
					maps = new HashMap<String, DField>();
					for (DField df : fields)
						maps.put(df.getName(), df);
				}
			}
		}
		return maps.get(name);
	}

	public boolean hasField(String name) {
		return null != getField(name);
	}

}
