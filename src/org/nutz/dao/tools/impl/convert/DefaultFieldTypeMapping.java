package org.nutz.dao.tools.impl.convert;

import java.lang.reflect.Field;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.annotation.ColType;
import org.nutz.dao.tools.annotation.NotNull;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

public class DefaultFieldTypeMapping implements FieldTypeMapping {
	
	@Override
	public DField convert(Field f, DField df) {
		Column col = f.getAnnotation(Column.class);
		if (null == col || Strings.isBlank(col.value()))
			df.setName(f.getName());
		else
			df.setName(col.value());

		if (null != f.getAnnotation(NotNull.class))
			df.setNotNull(true);

		Default def = f.getAnnotation(Default.class);
		if (null != def)
			df.setDefaultValue(def.value());

		ColType colType = f.getAnnotation(ColType.class);
		if (null != colType) {
			df.setType(colType.value());
		} else {
			df.setType(mapType(f));
		}
		return df;
	}
	
	public String mapType(Field f){
		Mirror<?> ft = Mirror.me(f.getType());
		if (ft.isStringLike()) {
			return "VARCHAR(50)";
		} else if (ft.isIntLike()) {
			return "INT";
		} else if (ft.isDateTimeLike()) {
			return "TIMESTAMP";
		} else if (ft.isBoolean()) {
			return "BOOLEAN";
		} else if (ft.isEnum()) {
			return "VARCHAR(20)";
		} else {
			return "???";
		}
	}
}
