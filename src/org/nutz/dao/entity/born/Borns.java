package org.nutz.dao.entity.born;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.regex.Pattern;

import org.nutz.dao.entity.Borning;
import org.nutz.dao.entity.Entity;
import org.nutz.lang.Lang;

public class Borns {

	public static Borning evalBorning(Entity<?> entity) {
		Class<?> type = entity.getType();
		Method rsMethod = null;
		Method rsFmMethod = null;
		Method defMethod = null;
		for (Method method : entity.getMirror().getStaticMethods()) {
			if (entity.getMirror().is(method.getReturnType())) {
				Class<?>[] pts = method.getParameterTypes();
				if (pts.length == 0)
					defMethod = method;
				else if (pts.length == 2 && pts[0] == ResultSet.class && pts[1] == Pattern.class)
					rsFmMethod = method;
				else if (pts.length == 1 && pts[0] == ResultSet.class)
					rsMethod = method;
			}
		}
		if (null != rsFmMethod) {
			// POJO.xxx(ResultSet,FieldMatcher)
			return new FMStaticResultSetMethodBorning(rsFmMethod);
		} else if (null != rsMethod) {
			// POJO.xxx(ResultSet)
			return new StaticResultSetMethodBorning(rsMethod);
		} else {
			try {
				// new POJO(ResultSet,FieldMatcher)
				try {
					return new FMResultSetConstructorBorning(type.getConstructor(ResultSet.class,
							Pattern.class));
				} catch (Throwable e) {
					// new POJO(ResultSet)
					return new ResultSetConstructorBorning(type.getConstructor(ResultSet.class));
				}
			} catch (Throwable e) {
				if (null != defMethod) // static POJO
					// POJO.getInstance();
					return new DefaultStaticMethodBorning(entity, defMethod);
				else
					try {
						// new POJO()
						return new DefaultConstructorBorning(entity, type.getConstructor());
					} catch (Exception e1) {
						throw Lang
								.makeThrow(
										"Entity [%s] is invailid, it should has at least one of:"
												+ " \n1. %s \n2.%s \n3. %s \n4. %s, \n(%s)",
										type.getName(),
										"Accessable constructor with one parameter type as java.sql.ResultSet, another parameter type is org.nutz.dao.FiledMatcher(optional)",
										"Accessable static method with one parameter type as java.sql.ResultSet, another parameter type is org.nutz.dao.FiledMatcher(optional) and return type is ["
												+ type.getName() + "]",
										"Accessable static method without parameter and return type is ["
												+ type.getName() + "]",
										"Accessable default constructor",
										"I will try to invoke those borning methods following the order above.");
					}
			}
		}
	}

}
