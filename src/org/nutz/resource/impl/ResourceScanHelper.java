package org.nutz.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.plugin.PluginManager;
import org.nutz.plugin.SimplePluginManager;
import org.nutz.resource.NutResource;
import org.nutz.resource.ResourceScan;

public final class ResourceScanHelper {
	
	private static final PluginManager<ResourceScan> scaners
		= new SimplePluginManager<ResourceScan>("org.nutz.resource.impl.FilesystemResourceScan",
				"org.nutz.resource.impl.JarResourceScan");
	
	public static List<NutResource> scanFiles(String src, String filter){
		List<NutResource> rList = new ArrayList<NutResource>();
		for (ResourceScan resourceScan : scaners.gets()) 
			rList.addAll(resourceScan.list(src, filter));
		return rList;
	}
	
	public static List<Class<?>> scanClasses(String packageZ) {
		List<NutResource> rList = ResourceScanHelper.scanFiles(packageZ.replace('.', '/'), ".class");
		List<Class<?>> list = new ArrayList<Class<?>>(rList.size());
		for (NutResource nutResource : rList) {
			try {
				String classFileName = nutResource.getName();
				String className = classFileName.substring(0,classFileName.length() - ".class".length()).replace('/', '.').replace('\\', '.');
				className = className.substring(className.indexOf(packageZ));
				list.add(Class.forName(className));
			}catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return list;
	}

}
