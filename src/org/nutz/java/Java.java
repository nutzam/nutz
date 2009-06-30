package org.nutz.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.meta.Email;

public class Java {

	public static void main(String[] args) {
		JavaClass pet = type(JavaClass.class, "com.zzh.pet.Pet");
		JavaClass srv = type(JavaClass.class, "com.zzh.pet.PetService");
		srv.addAnnotation(Id.class);
		srv.addAnnotation(ann(Table.class).setValue("TTT"));
		srv.setParent(type(JavaClass.class, "com.zzh.service.IdNameService"));
		srv.getParent().addTemplateParam(pet);
		JavaBodyMethod abc = method("abc");
		abc.addParam(param(HttpServletRequest.class, "request"));
		abc.addParam(param(HttpServletResponse.class, "response"));
		abc.addParam(param(Field.class, "field"));
		srv.addMethod(abc.addStatment(new JavaThrowExceptionStatment("ddd")));
		srv.addField(field(int.class, "id"));
		srv.addField(field(String.class, "name"));
		srv.addField(field(Calendar.class, "birthday"));
		srv.addField(field(Email.class, "email"));
		setters(getters(srv));
		System.out.println(renderSource(srv));
	}

	public static JavaParam param(Class<?> type, String name) {
		return new JavaParam(type(type), name);
	}

	public static JavaParam param(JavaType type, String name) {
		return new JavaParam(type, name);
	}

	public static JavaAnnotation ann(Class<? extends Annotation> annType) {
		JavaAnnotation jann = new JavaAnnotation();
		jann.setFullName(annType.getName());
		return jann;
	}

	public static JavaField field(Class<?> type, String name) {
		return new JavaField(type(type), name);
	}

	public static JavaField field(JavaType type, String name) {
		return new JavaField(type, name);
	}

	public static JavaConstructor constructor(JavaClass klass, JavaParam... params) {
		JavaConstructor c = new JavaConstructor(klass);
		for (JavaParam param : params)
			c.addParam(param);
		return c;
	}

	public static JavaConstructor defaultConstructor(JavaClass klass) {
		JavaConstructor c = new JavaConstructor(klass);
		c.addStatment(eval("super()"));
		return c;
	}

	public static JavaBodyMethod method(String name, JavaParam... params) {
		JavaBodyMethod method = new JavaBodyMethod();
		method.setName(name);
		for (JavaParam param : params)
			method.addParam(param);
		return method;
	}

	public static JavaClass getters(JavaClass klass) {
		for (JavaField field : klass.getFields())
			klass.addMethod(getter(field));
		return klass;
	}

	public static JavaClass setters(JavaClass klass) {
		for (JavaField field : klass.getFields())
			klass.addMethod(setter(field));
		return klass;
	}

	public static JavaClass adders(JavaClass klass) {
		for (JavaField field : klass.getFields())
			klass.addMethod(adder(klass, field));
		return klass;
	}

	public static JavaBodyMethod getter(JavaField field) {
		JavaBodyMethod method = new JavaBodyMethod();
		if (field.getType().isBoolean()) {
			if (field.getName().startsWith("is")) {
				method.setName(field.getName());
			} else {
				method.setName("is" + Strings.capitalize(field.getName()));
			}
		} else
			method.setName("get" + Strings.capitalize(field.getName()));
		method.setReturnType(field.getType());
		method.addStatment(new JavaReturnStatement().setVarName(field.getName()));
		return method;
	}

	public static JavaBodyMethod setter(JavaField field) {
		JavaBodyMethod method = new JavaBodyMethod();
		if (field.getType().isBoolean() && field.getName().startsWith("is")) {
			method.setName("set" + Strings.capitalize(field.getName().substring(2)));
		} else {
			method.setName("set" + Strings.capitalize(field.getName()));
		}
		method.addParam(param(field.getType(), field.getName()));
		method.addStatment(new JavaAssignStatement().setLeft("this." + field.getName()).setRight(
				field.getName()));
		return method;
	}

	public static JavaBodyMethod adder(JavaType returnType, JavaField field) {
		JavaBodyMethod method = setter(field);
		method.setReturnType(returnType);
		method.addStatment(new JavaReturnStatement().setVarName("this"));
		return method;
	}

	public static <T extends JavaType> T type(Class<T> type, String typeName) {
		T jt;
		try {
			jt = type.newInstance();
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		jt.setFullName(typeName);
		return jt;
	}

	public static JavaType type(Class<?> klass) {
		if (klass.isInterface())
			return type(JavaInterface.class, klass.getName());
		else if (klass.isAnnotation())
			return type(JavaAnnotation.class, klass.getName());
		else if (klass.isPrimitive())
			return type(JavaPrimitiveType.class, klass.getName());
		else if (klass.isEnum())
			throw new RuntimeException("Dont support!");
		else
			return type(JavaClass.class, klass.getName());
	}

	public static JavaStatement assign(String left, String right) {
		return new JavaAssignStatement().setLeft(left).setRight(right);
	}

	public static JavaStatement eval(String code) {
		return new JavaEvalStatement(code);
	}

	public static JavaStatement needImpl() {
		return new JavaThrowExceptionStatment("@TODO implement it");
	}

	public static <T extends JavaBodyMethod> T override(T method, JavaStatement... stats) {
		String superName = method.isReturnVoid() ? "super" : "return super";
		if (!(method instanceof JavaConstructor))
			method.addAnnotation(Java.ann(Override.class));
		else
			superName += "." + method.getName();
		if (null == stats || stats.length == 0) {
			StringBuilder sb = new StringBuilder(superName).append('(');
			if (method.getParams().size() > 0) {
				for (JavaParam param : method.getParams())
					sb.append(param.getName()).append(", ");
				sb.delete(sb.length() - 2, sb.length());
			}
			sb.append(')');
			method.addStatment(eval(sb.toString()));
		} else
			method.addStatment(needImpl());
		return method;
	}

	public static List<JavaType> formatImport(JavaType type) {
		Set<JavaType> set = type.getDependents();
		ArrayList<JavaType> list = new ArrayList<JavaType>(set.size() * 2);
		if (set.size() > 0) {
			JavaType[] ary = set.toArray(new JavaType[set.size()]);
			Arrays.sort(ary);
			for (int i = 0; i < (ary.length - 1); i++) {
				JavaType me = ary[i];
				if (me instanceof JavaPrimitiveType)
					continue;
				if (me instanceof JavaTemplateType)
					continue;
				if (me.getPackage().equals("java.lang"))
					continue;
				if (type.getPackage().equals(me.getPackage()))
					continue;
				JavaType next = ary[i + 1];
				list.add(me);
				if (me.getPackage().match(next.getPackage()) < 2)
					list.add(null);
			}
			list.add(ary[ary.length - 1]);
		}
		return list;
	}

	public static String getSourcePath(JavaType type) {
		return type.getFullName().replace('.', '/') + ".java";
	}

	public static String renderSource(JavaLanguageObject jlo) {
		return jlo.renderSource();
	}

	/**
	 * @param string
	 * @return
	 */
	public static String tab(String str) {
		return str.replaceAll("\n", "\n\t");
	}

}
