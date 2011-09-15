package org.nutz.json.meta;

import org.nutz.json.JsonField;

public class TestBy {

	private long id;
	@JsonField(by="org.nutz.json.JsonTest#justOK")
	private Object obj;
	
	@JsonField(by="me")
	private Object obj2;
	
	public String me(){
		return "Wendal";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public Object getObj2() {
		return obj2;
	}

	public void setObj2(Object obj2) {
		this.obj2 = obj2;
	}
}
