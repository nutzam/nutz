package org.nutz.log;

import org.nutz.log.impl.AbstractLogAdapter;
import org.nutz.log.impl.NullLog;
import org.nutz.log.impl.SystemLog;
import org.nutz.plugin.NutPluginManagement;

/**
 * 获取 Log 的静态工厂方法
 * 
 * @author Young(sunonfire@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 */
public class LogFactory {

	private static Log systemLog = new SystemLog();
	
	private static Log nullLog = new NullLog();

	private static LogAdapter workableAdapter;

	static {
		Object [] adapterArray = NutPluginManagement.getPlugins(LogAdapter.class);
		if(adapterArray != null && adapterArray.length > 0)
			workableAdapter = (LogAdapter) adapterArray[0];
	}

	public static void setLogAdapter(AbstractLogAdapter adapter) {
		if(adapter.canWork(null))
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
		if(useNullLog)
			return nullLog;
		return systemLog;
	}
	
	public static Log getRootLog(){
		if (workableAdapter != null) 
			return workableAdapter.getRootLogger();
		if(useNullLog)
			return nullLog;
		return systemLog;
	}
	
	public void useNullLog(boolean flag){
		useNullLog = flag;
	}
	
	private static boolean useNullLog = false;
}
