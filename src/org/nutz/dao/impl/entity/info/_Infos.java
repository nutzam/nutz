package org.nutz.dao.impl.entity.info;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Next;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Readonly;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.eject.EjectByField;
import org.nutz.lang.eject.EjectByGetter;
import org.nutz.lang.inject.InjectByField;
import org.nutz.lang.inject.InjectBySetter;

public class _Infos {

	private static <T extends FieldInfo> T create(Class<T> classOfT, Field field) {
		T info = Mirror.me(classOfT).born();
		info.name = field.getName();
		info.fieldType = field.getGenericType();
		info.injecting = new InjectByField(field);
		info.ejecting = new EjectByField(field);
		return info;
	}

	private static <T extends FieldInfo> T create(Class<T> classOfT, Method method) {
		T info = Mirror.me(classOfT).born();
		String name = method.getName();
		Method setter;
		Method getter;
		// 是 getter
		if (name.startsWith("get") && method.getParameterTypes().length == 0) {
			getter = method;
			name = Strings.lowerFirst(name.substring(4));
			// 寻找 setter
			try {
				setter = method.getDeclaringClass().getMethod(	"set" + Strings.capitalize(name),
																method.getReturnType());
			}
			catch (Exception e) {
				throw Lang.makeThrow(	"Method '%s'(%s) has '@Column', but NO setter!",
										method.getName(),
										method.getDeclaringClass().getName());
			}

		}
		// 布尔的 getter
		else if (name.startsWith("is")
					&& Mirror.me(method.getReturnType()).isBoolean()
					&& method.getParameterTypes().length == 0) {
			getter = method;
			name = Strings.lowerFirst(name.substring(3));
			// 寻找 setter
			try {
				setter = method.getDeclaringClass().getMethod(	"set" + Strings.capitalize(name),
																method.getReturnType());
			}
			catch (Exception e) {
				throw Lang.makeThrow(	"Method '%s'(%s) has '@Column', but NO setter!",
										method.getName(),
										method.getDeclaringClass().getName());
			}
		}
		// 是 setter
		else if (name.startsWith("set") && method.getParameterTypes().length == 1) {
			setter = method;
			name = Strings.lowerFirst(name.substring(4));
			// 寻找 getter
			try {
				getter = method.getDeclaringClass().getMethod("get" + Strings.capitalize(name));
			}
			catch (Exception e) {
				throw Lang.makeThrow(	"Method '%s'(%s) has '@Column', but NO getter!",
										method.getName(),
										method.getDeclaringClass().getName());
			}

		}
		// 靠，这哥们一定把 '@Column' 写错地方了，抛个异常提醒下丫的
		else {
			throw Lang.makeThrow(	"Method '%s'(%s) can not add '@Column', it MUST be a setter or getter!",
									method.getName(),
									method.getDeclaringClass().getName());
		}
		info.name = name;
		info.fieldType = getter.getGenericReturnType();
		info.ejecting = new EjectByGetter(getter);
		info.injecting = new InjectBySetter(setter);
		return info;
	}

	public static LinkInfo createLinkInfo(Method method) {
		LinkInfo info = create(LinkInfo.class, method);
		info.one = method.getAnnotation(One.class);
		info.many = method.getAnnotation(Many.class);
		info.manymany = method.getAnnotation(ManyMany.class);
		return info;
	}

	public static LinkInfo createLinkInfo(Field field) {
		LinkInfo info = create(LinkInfo.class, field);
		info.one = field.getAnnotation(One.class);
		info.many = field.getAnnotation(Many.class);
		info.manymany = field.getAnnotation(ManyMany.class);
		return info;
	}

	/**
	 * 根据 getter/setter 函数获取一个实体字段信息对象
	 * 
	 * @param pk
	 *            复合主键
	 * @param method
	 *            方法：可能是 getter 或者是 setter
	 * @return 字段信息对象
	 */
	public static MappingInfo createMapingInfo(PK pk, Method method) {
		MappingInfo info = create(MappingInfo.class, method);
		info.annPK = pk;
		info.annColumn = method.getAnnotation(Column.class);
		info.annDefine = method.getAnnotation(ColDefine.class);
		info.annDefault = method.getAnnotation(Default.class);
		info.annId = method.getAnnotation(Id.class);
		info.annName = method.getAnnotation(Name.class);
		info.annNext = method.getAnnotation(Next.class);
		info.annPrev = method.getAnnotation(Prev.class);
		info.annReadonly = method.getAnnotation(Readonly.class);
		return info;
	}

	/**
	 * 根据 Java 字段创建一个实体字段信息对象
	 * 
	 * @param pk
	 *            复合主键
	 * @param field
	 *            Java 字段
	 * @return 字段信息对象
	 */
	public static MappingInfo createMappingInfo(PK pk, Field field) {
		MappingInfo info = create(MappingInfo.class, field);
		info.annPK = pk;
		info.annColumn = field.getAnnotation(Column.class);
		info.annDefine = field.getAnnotation(ColDefine.class);
		info.annDefault = field.getAnnotation(Default.class);
		info.annId = field.getAnnotation(Id.class);
		info.annName = field.getAnnotation(Name.class);
		info.annNext = field.getAnnotation(Next.class);
		info.annPrev = field.getAnnotation(Prev.class);
		info.annReadonly = field.getAnnotation(Readonly.class);
		return info;
	}

}
