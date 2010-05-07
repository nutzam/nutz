package org.nutz.ioc.loader.annotation;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipFile;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.loader.xml.XmlIocLoader;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Resources;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class AnnotationIocLoader implements IocLoader {

	protected static final Log LOG = Logs.getLog(XmlIocLoader.class);

	private HashMap<String, IocObject> map = new HashMap<String, IocObject>();

	public AnnotationIocLoader(String... packages) {
		for (String packageZ : packages) {
			Package p = Package.getPackage(packageZ);
			if (p != null) {
				List<Class<?>> classList = Resources.scanClass(null, p);
				for (Class<?> classZ : classList)
					addClass(classZ);
			} else {
				File dir = Files.findFile(packageZ.replace('.', '/'));
				if (dir != null) {
					File[] files = Files.files(dir, ".class");
					if (files != null) {
						for (File file : files) {
							try {
								String className = packageZ +"."
										+ Files.getMajorName(file);
								addClass(Class.forName(className));
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}
				}
				else{
					String CLASSPATH = System.getenv().get("CLASSPATH");
					if (CLASSPATH != null){
						String [] paths = null;
						if (Lang.isWin())
							paths = CLASSPATH.split(";");
						else
							paths = CLASSPATH.split(":");
						try{
							String pathRegex = "/"+packageZ.replace('.', '/')+"/.+\\.class";
							for (String path : paths) {
								if (path.endsWith(".jar")){
									File file = new File(path);
									if (Files.isFile(file)){
										Files.findEntryInZip(new ZipFile(file), pathRegex);
									}
								}
							}
						}catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private void addClass(Class<?> classZ) {
		if (classZ.isInterface() || classZ.isMemberClass() || classZ.isEnum()
				|| classZ.isAnnotation() )
			return;
		int modify = classZ.getModifiers();
		if (Modifier.isAbstract(modify) || (!Modifier.isPublic(modify)))
			return;
		IocBean iocBean = classZ.getAnnotation(IocBean.class);
		if (iocBean != null) {
			String beanName = iocBean.name();
			if (Strings.isBlank(beanName)){
				String className = classZ.getSimpleName();
				if (className.length() > 1){
					beanName = (""+className.charAt(0)).toLowerCase()+className.substring(1) ;
				}else
					beanName = className.toLowerCase();
			}
			IocObject iocObject = new IocObject();
			iocObject.setType(classZ);
			map.put(beanName, iocObject);
			
			iocObject.setSingleton(iocBean.singleton());
			if ( ! Strings.isBlank(iocBean.scope()))
				iocObject.setScope(iocBean.scope());
			
			//配置构造方法
			if (iocBean.param().length > 0)
				for (String  value : iocBean.param())
					iocObject.addArg(convert(value));
			
			//设置Events
			IocEventSet eventSet = new IocEventSet();
			iocObject.setEvents(eventSet);
			if ( ! Strings.isBlank(iocBean.create()))
				eventSet.setCreate(iocBean.create().trim().intern());
			if ( ! Strings.isBlank(iocBean.depose()))
				eventSet.setCreate(iocBean.depose().trim().intern());
			if ( ! Strings.isBlank(iocBean.fetch()))
				eventSet.setCreate(iocBean.fetch().trim().intern());
			
			Field [] fields = classZ.getDeclaredFields();
			for (Field field : fields) {
				Inject inject = field.getAnnotation(Inject.class);
				if (inject == null)
					continue;
				IocField iocField = new IocField();
				iocField.setName(field.getName());
				
				iocField.setValue(convert(inject.value()));
			}
		}
	}
	
	protected IocValue convert(String value) {
		IocValue iocValue = new IocValue();
		iocValue.setType(value.substring(0,value.indexOf(":")));
		iocValue.setValue(value.substring(value.indexOf(":")+1));
		return iocValue;
	}

	public String[] getName() {
		return map.keySet().toArray(new String[map.size()]);
	}

	public boolean has(String name) {
		return map.containsKey(name);
	}

	public IocObject load(String name) throws ObjectLoadException {
		return map.get(name);
	}
}
