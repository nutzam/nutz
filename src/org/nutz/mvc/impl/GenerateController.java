package org.nutz.mvc.impl;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.mvc.annotation.Modules;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * 需要判断clazz是否需要增强 取得需要生成的方法(不检查dao是否存在,不存在直接初始化报错)
 * 生成相应方法，需要的参数有controllerName，domainName 重新生成class返回
 * @author tt
 */
public class GenerateController {
	private static final String[] initMethods = new String[]{
		"index","list","create","save","edit","update","show","delete","deleteAll"};
	private static final Map<String,Class<?>> classCache = new HashMap<String, Class<?>>();
	/**
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static Class<?> dump(final Class<?> clazz) throws Exception {

		if(!needDump(clazz)) return clazz;
		if(classCache.containsKey(clazz.getName())){  // for cache ，TODO 也可以改造classLoader,这需要好好设计一下
			return classCache.get(clazz.getName());
		}
		final Context ctx = Lang.context();
		String ctrName = clazz.getSimpleName();
		String ctrFullName = clazz.getName().replace('.', '/');
		String dmName = Strings.lowerFirst(ctrName.substring(0,ctrName.indexOf("Controller")));
		String DmName = ctrName.substring(0,ctrName.indexOf("Controller"));
		String dmFullName = "domains/"+ctrName.substring(0,ctrName.indexOf("Controller"));
		
		ctx.set("ctrName", ctrName);
		ctx.set("ctrFullName", ctrFullName);
		ctx.set("dmName", dmName);
		ctx.set("DmName", DmName);
		ctx.set("dmFullName", dmFullName);
		ClassReader cr = new ClassReader(clazz.getResourceAsStream(clazz.getSimpleName()+".class"));
		ClassWriter cw = new ClassWriter(0);
		class ChangVersionAdapter extends ClassAdapter{

			public ChangVersionAdapter(ClassVisitor cv) {
				super(cv);
			}
			@Override
			public void visitEnd() {
				Method[] methods = clazz.getMethods();
				for(String mName : initMethods){
					boolean need = true;
					for(Method m : methods){
						if(m.getName().equals(mName)){
							need = false;
							break;
						}
					}
					if(need){
						if("index".equals(mName)){
							generateIndex(this,ctx);
						}else if("list".equals(mName)){
							generateList(this,ctx);
						}else if("create".equals(mName)){
							generateCreate(this,ctx);
						}else if("save".equals(mName)){
							generateSave(this,ctx);
						}else if("edit".equals(mName)){
							generateEdit(this,ctx);
						}else if("update".equals(mName)){
							generateUpdate(this,ctx);
						}else if("show".equals(mName)){
							generateShow(this,ctx);
						}else if("delete".equals(mName)){
							generateDelete(this,ctx);
						}else if("deleteAll".equals(mName)){
							generateDeleteAll(this,ctx);
						}
					}
				}
				super.visitEnd();
			}
		}
		cr.accept(new ChangVersionAdapter(cw), 0);
		Class<?> clazz2 = new MyClassLoader().defineClass(clazz.getName(),cw.toByteArray());
		classCache.put(clazz.getName(), clazz2);
		return clazz2;
	}

	public static boolean needDump(Class<?> clazz) {
		if (clazz == null)
			return false;
		if(clazz.getAnnotation(Modules.class) != null){ // 不处理主模块
			return false;
		}
		if (clazz.getName().length() > "Controller".length()
				&& clazz.getName().endsWith("Controller")) {
			try {
				Field f0 = clazz.getField("SCOFFOLDING");
				boolean need = f0.getBoolean(null);
				return need;
			} catch (SecurityException e) {
			} catch (NoSuchFieldException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
		} 
		return false;
	}

	private static void generateIndex(ClassVisitor cr,Context ctx) {
		{
			AnnotationVisitor av0 = null;
			MethodVisitor mv = cr.visitMethod(ACC_PUBLIC, "index", "()V", null,
					null);
			{
				av0 = mv.visitAnnotation(
						"Lorg/nutz/mvc/annotation/Ok;", true);
				av0.visit("value", ">>:/"+ctx.getString("dmName")+"/list");
				av0.visitEnd();
			}
			{
				av0 = mv.visitAnnotation("Lorg/nutz/mvc/annotation/Scoffold;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 1);
			mv.visitEnd();
		}
	}

	private static void generateList(ClassVisitor cr,Context ctx) {
		{
			AnnotationVisitor av0 = null;
			MethodVisitor mv = cr
					.visitMethod(ACC_PUBLIC, "list", "(II)Lorg/nutz/mvc/util/PageForm;",
							"(II)Lorg/nutz/mvc/util/PageForm<L"+ctx.getString("dmFullName")+";>;", null);
			{
				av0 = mv.visitAnnotation("Lorg/nutz/mvc/annotation/Scoffold;", true);
				av0.visitEnd();
			}
			{
				av0 = mv.visitParameterAnnotation(0,
						"Lorg/nutz/mvc/annotation/Param;", true);
				av0.visit("value", "offset");
				av0.visitEnd();
			}
			{
				av0 = mv.visitParameterAnnotation(1,
						"Lorg/nutz/mvc/annotation/Param;", true);
				av0.visit("value", "max");
				av0.visitEnd();
			}
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, ctx.getString("ctrFullName"), "dao",
					"Lorg/nutz/dao/Dao;");
			mv.visitLdcInsn(Type.getType("L"+ctx.getString("dmFullName")+";"));
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC, "org/nutz/mvc/util/PageForm", "getPaper",
					"(Lorg/nutz/dao/Dao;Ljava/lang/Class;Lorg/nutz/dao/Condition;II)Lorg/nutz/mvc/util/PageForm;");
			mv.visitVarInsn(ASTORE, 3);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(5, 4);
			mv.visitEnd();
		}
	}

	private static void generateCreate(ClassVisitor cv,Context ctx) {
		{
			AnnotationVisitor av0 = null;
			MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "create", "()V", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/nutz/mvc/annotation/Scoffold;", true);
				av0.visitEnd();
			}
			mv.visitCode();
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 1);
			mv.visitEnd();
		}
	}

	private static void generateSave(ClassVisitor cv,Context ctx) {
		{
			MethodVisitor mv = null;
			AnnotationVisitor av0 = null;
			mv = cv.visitMethod(ACC_PUBLIC, "save",
					"(L"+ctx.getString("dmFullName")+";)Ljava/lang/Object;", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/nutz/mvc/annotation/Ok;", true);
				av0.visit("value", ">>:/"+ctx.getString("dmName")+"/list");
				av0.visitEnd();
			}
			{
				av0 = mv.visitAnnotation("Lorg/nutz/mvc/annotation/Scoffold;", true);
				av0.visitEnd();
			}
			{
				av0 = mv.visitParameterAnnotation(0,
						"Lorg/nutz/mvc/annotation/Param;", true);
				av0.visit("value", "..");
				av0.visitEnd();
			}
			mv.visitCode();
			mv.visitLdcInsn("");
			mv.visitVarInsn(ASTORE, 2);
			mv.visitVarInsn(ALOAD, 1);
			Label l0 = new Label();
			mv.visitJumpInsn(IFNULL, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, ctx.getString("ctrFullName"), "dao",
					"Lorg/nutz/dao/Dao;");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/nutz/dao/Dao", "insert",
					"(Ljava/lang/Object;)Ljava/lang/Object;");
			mv.visitInsn(POP);
			mv.visitLdcInsn("\u63d2\u5165\u6210\u529f");
			mv.visitVarInsn(ASTORE, 2);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitInsn(ARETURN);
			mv.visitLabel(l0);
			mv.visitFrame(Opcodes.F_APPEND, 1,
					new Object[] { "java/lang/String" }, 0, null);
			mv.visitLdcInsn("\u6821\u9a8c\u4e0d\u6210\u529f");
			mv.visitVarInsn(ASTORE, 2);
			mv.visitLdcInsn("/"+ctx.getString("dmName")+"/create");
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC, "org/nutz/mvc/util/CV", "redirect",
					"(Ljava/lang/String;Ljava/lang/Object;)Lorg/nutz/mvc/View;");
			mv.visitInsn(ARETURN);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
	}

	private static void generateEdit(ClassVisitor cv,Context ctx) {
		{
			MethodVisitor mv = null;
			AnnotationVisitor av0 = null;
			mv = cv.visitMethod(ACC_PUBLIC, "edit", "(J)Ljava/lang/Object;",
					null, null);
			{
				av0 = mv.visitAnnotation("Lorg/nutz/mvc/annotation/Scoffold;", true);
				av0.visitEnd();
			}
			{
				av0 = mv.visitParameterAnnotation(0,
						"Lorg/nutz/mvc/annotation/Param;", true);
				av0.visit("value", "id");
				av0.visitEnd();
			}
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, ctx.getString("ctrFullName"), "dao",
					"Lorg/nutz/dao/Dao;");
			mv.visitLdcInsn(Type.getType("L"+ctx.getString("dmFullName")+";"));
			mv.visitVarInsn(LLOAD, 1);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/nutz/dao/Dao", "fetch",
					"(Ljava/lang/Class;J)Ljava/lang/Object;");
			mv.visitTypeInsn(CHECKCAST, ""+ctx.getString("dmFullName")+"");
			mv.visitVarInsn(ASTORE, 3);
			mv.visitVarInsn(ALOAD, 3);
			Label l0 = new Label();
			mv.visitJumpInsn(IFNONNULL, l0);
			mv.visitLdcInsn("/"+ctx.getString("dmName")+"/list");
			mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
			mv.visitInsn(DUP);
			mv.visitLdcInsn("\u8be5");
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder",
					"<init>", "(Ljava/lang/String;)V");
			mv.visitLdcInsn(ctx.getString("DmName")+".listName");
			mv.visitMethodInsn(INVOKESTATIC, "org/nutz/mvc/util/LocalMessage", "get",
					"(Ljava/lang/String;)Ljava/lang/Object;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
					"append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
			mv.visitLdcInsn("\u4e0d\u5b58\u5728");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
					"append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
					"toString", "()Ljava/lang/String;");
			mv.visitMethodInsn(INVOKESTATIC, "org/nutz/mvc/util/CV", "redirect",
					"(Ljava/lang/String;Ljava/lang/Object;)Lorg/nutz/mvc/View;");
			mv.visitInsn(ARETURN);
			mv.visitLabel(l0);
			mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { ""+ctx.getString("dmFullName")+"" }, 0,
					null);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(4, 4);
			mv.visitEnd();
		}
	}

	private static void generateUpdate(ClassVisitor cv,Context ctx) {
		{
			MethodVisitor mv = null;
			AnnotationVisitor av0 = null;
			mv = cv.visitMethod(ACC_PUBLIC, "update",
					"(L"+ctx.getString("dmFullName")+";)Ljava/lang/Object;", null, null);
			{
				av0 = mv.visitAnnotation("Lorg/nutz/mvc/annotation/Ok;", true);
				av0.visit("value", ">>:/"+ctx.getString("dmName")+"/list");
				av0.visitEnd();
			}
			{
				av0 = mv.visitAnnotation("Lorg/nutz/mvc/annotation/Scoffold;", true);
				av0.visitEnd();
			}
			{
				av0 = mv.visitParameterAnnotation(0,
						"Lorg/nutz/mvc/annotation/Param;", true);
				av0.visit("value", "..");
				av0.visitEnd();
			}
			mv.visitCode();
			mv.visitLdcInsn("");
			mv.visitVarInsn(ASTORE, 2);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, ""+ctx.getString("dmFullName")+"", "getId",
					"()Ljava/lang/Integer;");
			Label l0 = new Label();
			mv.visitJumpInsn(IFNULL, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, ctx.getString("ctrFullName"), "dao",
					"Lorg/nutz/dao/Dao;");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/nutz/dao/Dao", "update",
					"(Ljava/lang/Object;)I");
			mv.visitInsn(POP);
			mv.visitLdcInsn("\u66f4\u65b0\u6210\u529f");
			mv.visitVarInsn(ASTORE, 2);
			Label l1 = new Label();
			mv.visitJumpInsn(GOTO, l1);
			mv.visitLabel(l0);
			mv.visitFrame(Opcodes.F_APPEND, 1,
					new Object[] { "java/lang/String" }, 0, null);
			mv.visitLdcInsn("\u6821\u9a8c\u4e0d\u6210\u529f,\u8bf7\u91cd\u65b0\u8f93\u5165");
			mv.visitVarInsn(ASTORE, 2);
			mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
			mv.visitInsn(DUP);
			mv.visitLdcInsn("/"+ctx.getString("dmName")+"/edit?id=");
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder",
					"<init>", "(Ljava/lang/String;)V");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, ""+ctx.getString("dmFullName")+"", "getId",
					"()Ljava/lang/Integer;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
					"append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
					"toString", "()Ljava/lang/String;");
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC, "org/nutz/mvc/util/CV", "redirect",
					"(Ljava/lang/String;Ljava/lang/Object;)Lorg/nutz/mvc/View;");
			mv.visitInsn(ARETURN);
			mv.visitLabel(l1);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
	}
	private static void generateShow(ClassVisitor cv,Context ctx){
		MethodVisitor mv = null;
		AnnotationVisitor av0 = null;
		mv = cv.visitMethod(ACC_PUBLIC, "show", "(J)Ljava/lang/Object;",
				null, null);
		{
			av0 = mv.visitAnnotation("Lorg/nutz/mvc/annotation/Scoffold;", true);
			av0.visitEnd();
		}
		{
			av0 = mv.visitParameterAnnotation(0,
					"Lorg/nutz/mvc/annotation/Param;", true);
			av0.visit("value", "id");
			av0.visitEnd();
		}
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, ctx.getString("ctrFullName"), "dao",
				"Lorg/nutz/dao/Dao;");
		mv.visitLdcInsn(Type.getType("L"+ctx.getString("dmFullName")+";"));
		mv.visitVarInsn(LLOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "org/nutz/dao/Dao", "fetch",
				"(Ljava/lang/Class;J)Ljava/lang/Object;");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(4, 3);
		mv.visitEnd();
	}

	private static void generateDelete(ClassVisitor cv,Context ctx) {
		MethodVisitor mv = null;
		AnnotationVisitor av0 = null;
		mv = cv.visitMethod(ACC_PUBLIC, "delete",
				"(Ljava/lang/Long;)Ljava/lang/Object;", null, null);
		{
			av0 = mv.visitAnnotation("Lorg/nutz/mvc/annotation/Ok;", true);
			av0.visit("value", ">>:/"+ctx.getString("dmName")+"/list");
			av0.visitEnd();
		}
		{
			av0 = mv.visitAnnotation("Lorg/nutz/mvc/annotation/Scoffold;", true);
			av0.visitEnd();
		}
		{
			av0 = mv.visitParameterAnnotation(0,
					"Lorg/nutz/mvc/annotation/Param;", true);
			av0.visit("value", "id");
			av0.visitEnd();
		}
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, ctx.getString("ctrFullName"), "dao",
				"Lorg/nutz/dao/Dao;");
		mv.visitLdcInsn(Type.getType("L"+ctx.getString("dmFullName")+";"));
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue",
				"()J");
		mv.visitMethodInsn(INVOKEINTERFACE, "org/nutz/dao/Dao", "delete",
				"(Ljava/lang/Class;J)I");
		mv.visitInsn(POP);
		mv.visitLdcInsn("\u5220\u9664\u6210\u529f");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(4, 2);
		mv.visitEnd();
	}

	private static void generateDeleteAll(ClassVisitor cv,Context ctx) {
		MethodVisitor mv = null;
		AnnotationVisitor av0 = null;
		mv = cv.visitMethod(ACC_PUBLIC, "deleteAll",
				"(Ljava/lang/String;)Ljava/lang/Object;", null, null);
		{
			av0 = mv.visitAnnotation("Lorg/nutz/mvc/annotation/Ok;", true);
			av0.visit("value", ">>:/"+ctx.getString("dmName")+"/list");
			av0.visitEnd();
		}
		{
			av0 = mv.visitAnnotation("Lorg/nutz/mvc/annotation/Scoffold;", true);
			av0.visitEnd();
		}
		{
			av0 = mv.visitParameterAnnotation(0,
					"Lorg/nutz/mvc/annotation/Param;", true);
			av0.visit("value", "ids");
			av0.visitEnd();
		}
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKESTATIC, "org/nutz/lang/Strings",
				"isEmpty", "(Ljava/lang/CharSequence;)Z");
		Label l0 = new Label();
		mv.visitJumpInsn(IFNE, l0);
		mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
		mv.visitInsn(DUP);
		mv.visitLdcInsn("delete from "+ctx.getString("dmName")+" where id in (");
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder",
				"<init>", "(Ljava/lang/String;)V");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
				"append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
		mv.visitLdcInsn(")");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
				"append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
				"toString", "()Ljava/lang/String;");
		mv.visitMethodInsn(INVOKESTATIC, "org/nutz/dao/Sqls", "create",
				"(Ljava/lang/String;)Lorg/nutz/dao/sql/Sql;");
		mv.visitVarInsn(ASTORE, 2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, ctx.getString("ctrFullName"), "dao",
				"Lorg/nutz/dao/Dao;");
		mv.visitInsn(ICONST_1);
		mv.visitTypeInsn(ANEWARRAY, "org/nutz/dao/sql/Sql");
		mv.visitInsn(DUP);
		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitInsn(AASTORE);
		mv.visitMethodInsn(INVOKEINTERFACE, "org/nutz/dao/Dao", "execute",
				"([Lorg/nutz/dao/sql/Sql;)V");
		mv.visitLabel(l0);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitLdcInsn("\u5220\u9664\u6210\u529f");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(5, 3);
		mv.visitEnd();
	}

	static class MyClassLoader extends ClassLoader {
		public MyClassLoader(){
			super(MyClassLoader.class.getClassLoader());
		}
		public Class<?> defineClass(String name ,byte[] buffer) {
			return super.defineClass(name, buffer, 0, buffer.length, null);
		}
	}
}
