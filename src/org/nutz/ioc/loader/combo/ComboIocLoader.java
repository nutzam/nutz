package org.nutz.ioc.loader.combo;

import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.Mirror;

/**
 * 融化多种IocLoader
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ComboIocLoader implements IocLoader {
	
	private List<IocLoader> iocLoaders = new ArrayList<IocLoader>();
	
	/**
	 * 这个构造方法需要一组特殊的参数
	 * <p/>第一种,以*开头,后面接类名, 如 <code>*org.nutz.ioc.loader.json.JsonLoader</code>
	 * <p/>第二种,为具体的参数
	 * <p/> 处理规律, 当遇到第一种参数(*),则认为接下来的一个或多个参数为这一个IocLoader的参数,直至遇到另外一个*开头的参数
	 * <p/><p/> 例子:<p/>
	 * <code>{"*org.nutz.ioc.loader.json.JsonLoader","dao.js","service.js","*org.nutz.ioc.loader.xml.XmlIocLoader","config.xml"}</code>
	 * <p/>这样的参数, 会生成一个以{"dao.js","service.js"}作为参数的JsonLoader,一个以{"dao.xml"}作为参数的XmlIocLoader
	 * @throws ClassNotFoundException 如果*开头的参数所指代的类不存在
	 */
	public ComboIocLoader(String...args) throws ClassNotFoundException {
		ArrayList<String> argsList = null;
		String currentClassName = null;
		for (String str : args) {
			if (str.startsWith("*")){
				if (argsList != null)
					createIocLoader(currentClassName, argsList);
				currentClassName = str.substring(1);
				argsList = new ArrayList<String>();
			}else 
				argsList.add(str);
		}
		if (currentClassName != null)
			createIocLoader(currentClassName, argsList);
	}
	
	private void createIocLoader(String className , List<String> args) throws ClassNotFoundException{
		iocLoaders.add((IocLoader) Mirror.me(Class.forName(className)).born(args.toArray(new Object[args.size()])));
	}
	
	public ComboIocLoader(IocLoader...loaders) {
		for (IocLoader iocLoader : loaders) 
			if (iocLoader != null)
				iocLoaders.add(iocLoader);
	}

	public String[] getName() {
		ArrayList<String> list = new ArrayList<String>();
		for (IocLoader iocLoader : iocLoaders) {
			for (String name : iocLoader.getName())
				list.add(name);
		}
		return list.toArray(new String[list.size()]);
	}

	public boolean has(String name) {
		for (IocLoader iocLoader : iocLoaders)
			if (iocLoader.has(name))
				return true;
		return false;
	}

	public IocObject load(String name) throws ObjectLoadException {
		for (IocLoader iocLoader : iocLoaders)
			if (iocLoader.has(name))
				return iocLoader.load(name);
		return null;
	}

}
