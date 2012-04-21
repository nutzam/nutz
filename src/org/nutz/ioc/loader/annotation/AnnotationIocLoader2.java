package org.nutz.ioc.loader.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.ioc.IocException;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

/**
 * 基于注解的Ioc配置
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class AnnotationIocLoader2 implements IocLoader {

	private static final Log LOG = Logs.get();

	private HashMap<String, IocObject> map = new HashMap<String, IocObject>();

	public AnnotationIocLoader2(String... packages) {
		for (String packageZ : packages)
			for (Class<?> classZ : Scans.me().scanPackage(packageZ))
				addClass(classZ);
		if (LOG.isInfoEnabled())
			LOG.infof(	"Scan complete ! Found %s classes in %s base-packages!\nbeans = %s",
						map.size(),
						packages.length,
						Castors.me().castToString(map.keySet()));
	}

	private void addClass(Class<?> classZ) {
		if (classZ.isInterface()
			|| classZ.isMemberClass()
			|| classZ.isEnum()
			|| classZ.isAnnotation()
			|| classZ.isAnonymousClass())
			return;
		int modify = classZ.getModifiers();
		if (Modifier.isAbstract(modify) || (!Modifier.isPublic(modify)))
			return;
		IocBean iocBean = classZ.getAnnotation(IocBean.class);
		if (iocBean != null) {
			if (LOG.isDebugEnabled())
				LOG.debugf("Found a Class with Ioc-Annotation : %s", classZ);
		}
		String beanName = null;
		if(iocBean != null){
			// 采用 @IocBean->name
			beanName = iocBean.name();
			if (Strings.isBlank(beanName)) {
				// 否则采用 @InjectName
				InjectName innm = classZ.getAnnotation(InjectName.class);
				if (null != innm && !Strings.isBlank(innm.value())) {
					beanName = innm.value();
				}
				// 大哥（姐），您都不设啊!? 那就用 simpleName 吧
				else {
					beanName = Strings.lowerFirst(classZ.getSimpleName());
				}
			}
		}else{
			String clazzName = classZ.getName();
			if(clazzName.endsWith("Controller") && clazzName.length()>"Controller".length()){
				beanName = classZ.getName();
			}
			if(clazzName.endsWith("Service") && clazzName.length()>"Service".length()){
				beanName = Strings.lowerFirst(classZ.getSimpleName());
			}
		}
		IocObject iocObject = new IocObject();
		iocObject.setType(classZ);
		map.put(beanName, iocObject);

		if(iocBean != null){
			iocObject.setSingleton(iocBean.singleton());
			if (!Strings.isBlank(iocBean.scope()))
				iocObject.setScope(iocBean.scope());
		}else{
			iocObject.setSingleton(true);
			iocObject.setScope("app");
		}
		
		if(iocBean != null){
			// 看看构造函数都需要什么函数
			String[] args = iocBean.args();
			if (null != args && args.length > 0)
				for (String value : args)
					iocObject.addArg(convert(value));
		}
		if(iocBean != null){
			// 设置Events
			IocEventSet eventSet = new IocEventSet();
			iocObject.setEvents(eventSet);
			if (!Strings.isBlank(iocBean.create()))
				eventSet.setCreate(iocBean.create().trim().intern());
			if (!Strings.isBlank(iocBean.depose()))
				eventSet.setDepose(iocBean.depose().trim().intern());
			if (!Strings.isBlank(iocBean.fetch()))
				eventSet.setFetch(iocBean.fetch().trim().intern());
		}
		// 处理字段(以@Inject方式,位于字段)
		List<String> fieldList = new ArrayList<String>();
		Mirror<?> mirror = Mirror.me(classZ);
		Field[] fields = mirror.getFields(Inject.class);
		for (Field field : fields) {
			Inject inject = field.getAnnotation(Inject.class);
			//无需检查,因为字段名是唯一的
			//if(fieldList.contains(field.getName()))
			//	throw duplicateField(classZ,field.getName());
			IocField iocField = new IocField();
			iocField.setName(field.getName());
			IocValue iocValue;
			if (Strings.isBlank(inject.value())) {
				iocValue = new IocValue();
				iocValue.setType(IocValue.TYPE_REFER);
				iocValue.setValue(field.getName());
			} else
				iocValue = convert(inject.value());
			iocField.setValue(iocValue);
			iocObject.addField(iocField);
			fieldList.add(iocField.getName());
		}
		// 处理字段(以@Inject方式,位于set方法)
		Method[] methods = mirror.getMethods();
		for (Method method : methods) {
			Inject inject = method.getAnnotation(Inject.class);
			
			//过滤特殊方法
			int m = method.getModifiers();
			if(Modifier.isAbstract(m) || (!Modifier.isPublic(m))
					|| Modifier.isStatic(m))
				continue;
			if (method.getName().startsWith("set") 
				&& method.getName().length() > 3
				&& method.getParameterTypes().length == 1) {
				IocField iocField = new IocField();
				iocField.setName(Strings.lowerFirst(method.getName().substring(3)));
				if(fieldList.contains(iocField.getName()))
					if(inject != null)
						throw duplicateField(classZ,iocField.getName());
					else
						continue;
				IocValue iocValue;
				if (inject == null || Strings.isBlank(inject.value())) {
					iocValue = new IocValue();
					iocValue.setType(IocValue.TYPE_REFER);
					iocValue.setValue(Strings.lowerFirst(method.getName().substring(3)));
				} else
					iocValue = convert(inject.value());
				iocField.setValue(iocValue);
				iocObject.addField(iocField);
				fieldList.add(iocField.getName());
			}
		}
		if(iocBean != null){
			// 处理字段(以@IocBean.field方式),只允许引用同名的bean, 就映射为 refer:FieldName
			String[] flds = iocBean.fields();
//			if (null == flds || flds.length == 0) {
//				flds = iocBean.field();
//			}
			if (flds != null && flds.length > 0) {
				for (String fieldInfo : flds) {
					if (fieldList.contains(fieldInfo))
						throw duplicateField(classZ,fieldInfo);
					IocField iocField = new IocField();
					iocField.setName(fieldInfo);
					IocValue iocValue = new IocValue();
					iocValue.setType(IocValue.TYPE_REFER);
					iocValue.setValue(fieldInfo);
					iocField.setValue(iocValue);
					iocObject.addField(iocField);
					fieldList.add(iocField.getName());
				}
			}
		}
		if (LOG.isDebugEnabled())
			LOG.debugf("Processed Ioc Class : %s as [%s]", classZ, beanName);
	}

	protected IocValue convert(String value) {
		IocValue iocValue = new IocValue();
		if (value.indexOf(':') > -1) {
			iocValue.setType(value.substring(0, value.indexOf(':')));
			iocValue.setValue(value.substring(value.indexOf(':') + 1));
		} else
			iocValue.setValue(value);
		return iocValue;
	}

	public String[] getName() {
		return map.keySet().toArray(new String[map.size()]);
	}

	public boolean has(String name) {
		return map.containsKey(name);
	}

	public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
		if (has(name))
			return map.get(name);
		throw new ObjectLoadException("Object '" + name + "' without define!");
	}
	
	private static final IocException duplicateField(Class<?> classZ, String name){
		return Lang.makeThrow(IocException.class,	"Duplicate filed defined! Class=%s,FileName=%s",
				classZ,
				name);
	}
}
