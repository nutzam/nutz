package org.nutz.dao.tools;

public class DField {

	private String name;
	private String type;
	private String decorator;
	private boolean autoIncreament;
	private boolean primaryKey;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDecorator() {
		return decorator;
	}

	public void setDecorator(String decorator) {
		this.decorator = decorator;
	}

	public boolean isAutoIncreament() {
		return autoIncreament;
	}

	public void setAutoIncreament(boolean autoIncreament) {
		this.autoIncreament = autoIncreament;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

}
