package org.nutz.dao.tools.impl.convert;

import java.lang.reflect.Field;

import org.nutz.dao.tools.DField;

public interface FieldTypeMapping {

	DField convert(Field f, DField df);
}
