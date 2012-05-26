package org.nutz.template.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Files;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class ClassUtil {

	private static String projectHome = "";
	public static void setProjectHome(String home){
		projectHome = home;
	}
	public static Class<?> findClass(String name) throws IOException, ClassNotFoundException{
		if(name == null || name.trim().length() == 0){
			return null;
		}
		File sourceFile = new File(getSourcePath()+name.replaceAll("\\.", "/")+".java");
		if(sourceFile.exists())
			compileClass(name);
		List<URL> resources = new ArrayList<URL>();
		try {
			resources.add(new File(getTempPath()).toURI().toURL());
			File dir = new File(getLibPath());
			if(dir.exists() && dir.isDirectory()){
				for(File jar : dir.listFiles()){
					if(jar.isFile() && jar.getName().endsWith("jar")){
						resources.add(jar.toURI().toURL());
					}
				}
			}
		} catch (MalformedURLException e) {
			throw new IOException("the resource maybe has some errors",e);
		}
		NorClassLoader nor = new NorClassLoader(resources.toArray(new URL[resources.size()]),Thread.currentThread().getContextClassLoader());
		Class<?> clazz = nor.loadClass(name);
		Files.deleteDir(new File(getTempPath()));
		return clazz;
	}
	public static void compileClass(String name){
		String[] args = new String[]{"-extdirs",getLibPath(),
				"-sourcepath",getSourcePath(),
				"-6","-d",getTempPath(),
				getSourcePath()+name.replaceAll("\\.", "/")+".java"};
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		new org.eclipse.jdt.internal.compiler.batch.Main(new PrintWriter(baos),new PrintWriter(baos),false,null,null).compile(args);
		log.debug(baos.toString());
	}
	public static String getTempPath(){
		return projectHome+"build-temp/";
	}
	public static String getSourcePath(){
		return projectHome+"src/";
	}
	public static String getLibPath(){
		return projectHome+"webapp/WEB-INF/lib/";
	}
	private final static Log log = Logs.getLog(ClassUtil.class);
}
