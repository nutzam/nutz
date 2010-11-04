package org.nutz.dao.tools.impl.convert;

import java.lang.reflect.Field;

import org.nutz.lang.Mirror;

public class OracleFieldTypeMapping extends DefaultFieldTypeMapping {

	@Override
	public String mapType(Field f) {
		if (Mirror.me(f.getType()).isLong())
			return "bigint";
		return super.mapType(f);
	}
}
