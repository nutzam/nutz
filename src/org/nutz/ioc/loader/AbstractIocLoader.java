package org.nutz.ioc.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 提供基础IocLoader实现
 * <p/>1. parent依赖检查
 * <p/>2. parent继承的实现
 * @author wendal(wendal1985@gmail.com)
 *
 */
public abstract class AbstractIocLoader implements IocLoader {
	
	protected static final Log LOG = Logs.getLog(AbstractIocLoader.class);

	private TreeMap<String, IocObject> iocMap = new TreeMap<String, IocObject>();
	
	private Map<String, String> parentMap;

	public String[] getName() {
		return iocMap.keySet().toArray(new String[iocMap.keySet().size()]);
	}

	public boolean has(String name) {
		return iocMap.containsKey(name);
	}

	public IocObject load(String name) throws ObjectLoadException {
		IocObject iocObject = iocMap.get(name);
		if(iocObject == null)
			return null;
		return iocObject.clone();
	}
	
	protected void addIocObject(String beanId ,IocObject iocObject) {
		if (beanId == null)
			throw Lang.makeThrow("其中一个bean没有id!");
		if (this.has(beanId))
			throw Lang.makeThrow("发现重复的Bean id! id=" + beanId);
		iocMap.put(beanId, iocObject);
	}
	
	protected void clearPool() {
		iocMap.clear();
	}
	
	protected void setParentMap(Map<String, String> parentMap) {
		this.parentMap = parentMap;
	}

	protected void handleParent(){
		if(parentMap == null)
			return;
		//检查parentId是否都存在.
		for (String parentId : parentMap.values())
			if(! iocMap.containsKey(parentId))
				throw Lang.makeThrow("发现无效的parent=%s", parentId);
		//检查循环依赖
		List<String> parentList = new ArrayList<String>();
		for (Entry<String, String> entry : parentMap.entrySet()) {
			if(! check(parentList, entry.getKey()))
				throw Lang.makeThrow("发现循环依赖! bean id=%s", entry.getKey());
			parentList.clear();
		}
		while(parentMap.size() != 0){
			Iterator<Entry<String, String>> it = parentMap.entrySet().iterator();
			while(it.hasNext()){
				Entry<String, String> entry = it.next();
				String beanId = entry.getKey();
				String parentId = entry.getValue();
				if(parentMap.get(parentId) == null){
					IocObject newIocObject = Iocs.mergeWith(iocMap.get(beanId), iocMap.get(parentId));
					iocMap.put(beanId, newIocObject);
					it.remove();
				}
			}
		}
	}
	
	private boolean check(List<String> parentList,String currentBeanId){
		if(parentList.contains(currentBeanId))
			return false;
		String parentBeanId = parentMap.get(currentBeanId);
		if(parentBeanId == null)
			return true;
		parentList.add(currentBeanId);
		return check(parentList, parentBeanId);
	}
}
