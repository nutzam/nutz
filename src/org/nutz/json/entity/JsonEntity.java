package org.nutz.json.entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Mirror;
import org.nutz.lang.born.Borning;
import org.nutz.lang.born.BorningException;

/**
 * 记录一个Java如何映射 JSON 字符串的规则
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class JsonEntity {

	private List<JsonEntityField> fields;

	private Borning<?> borning;

	private BorningException err;

	public JsonEntity(Mirror<?> mirror) {
		Field[] flds = mirror.getFields();
		fields = new ArrayList<JsonEntityField>(flds.length);
		for (Field fld : flds) {
			JsonEntityField ef = JsonEntityField.eval(mirror, fld);
			if (null != ef)
				fields.add(ef);
		}

		try {
			borning = mirror.getBorning();
		}
		catch (BorningException e) {
			err = e;
		}
	}

	public List<JsonEntityField> getFields() {
		return fields;
	}

	public Object born() {
		if (null == borning)
			throw err;
		return borning.born(new Object[0]);
	}

}
