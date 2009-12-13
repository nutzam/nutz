package org.nutz.log;

import org.nutz.log.impl.AbstractLogAdapter;
import org.nutz.log.impl.SystemLog;
import org.nutz.plugin.PluginManager;

/**
 * 获取 Log 的静态工厂方法
 * 
 * @author Young(sunonfire@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class LogFactory {

	private static SystemLog systemLog = SystemLog.me();

	private static LogAdapter workableAdapter;

	static {
		LogAdapter [] adapterArray = PluginManager.get(LogAdapter.class);
		if(adapterArray != null && adapterArray.length > 0)
			workableAdapter = adapterArray[0];
	}

	public static void setLogAdapter(AbstractLogAdapter adapter) {
		if(adapter.canWork())
			workableAdapter = adapter;
	}

	public static Log getLog(Class<?> clazz) {
		if(clazz == null)
			return getRootLog();
		return getLog(clazz.getName());
	}

	public static Log getLog(String className) {
		if(className == null)
			return getRootLog();
		if (workableAdapter != null) 
			return workableAdapter.getLogger(className);
		return systemLog;
	}
	
	public static Log getRootLog(){
		if (workableAdapter != null) 
			return workableAdapter.getRootLogger();
		return systemLog;
	}
	
	public void needOutput(boolean flag){
		systemLog.needOutput(flag);
	}
}
