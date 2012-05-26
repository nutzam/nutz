package org.nutz.template.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class NorClassLoader extends ClassLoader{
	
	URL[] urls = null;
	public NorClassLoader(){
		super();
	}
	public NorClassLoader(URL[] urls){
		this.urls = urls;
	}
	public NorClassLoader(URL[] urls,ClassLoader parent){
		super(parent);
		this.urls = urls;
	}
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> clazz = null;
		if(name == null || name.length()==0) 
			throw new ClassNotFoundException("class <"+name+"> can't found");
		
		try{
			byte[] buffer = searchResource(name);
			if(buffer == null){
				throw new ClassNotFoundException("class <"+name+"> can't found");
			}
			clazz = defineClass(null, buffer, 0, buffer.length);
			
		}catch(Exception e){
			throw new ClassNotFoundException("class <"+name+"> can't found",e);
		}
		if(clazz == null){
			throw new ClassNotFoundException("class <"+name+"> can't found");
		}
		return clazz;
	}
	private byte[] searchResource(String name) throws IOException{
		if(urls == null || urls.length ==0){
			throw new NullPointerException("urls can't be null or empty");
		}
		String pathName = name.replaceAll("\\.", "/");
		ByteArrayOutputStream baos = null;
		for(URL url : urls){
			try {
				File file = new File(url.toURI());
				if(file.exists() && file.isDirectory()){// source folder
					File clazzFile = new File(file,pathName+".class");
					if(clazzFile.exists() && clazzFile.isFile()){
						baos = new ByteArrayOutputStream();
						FileInputStream fis = new FileInputStream(clazzFile);
						int b = 0;
						while((b=fis.read()) >=0){
							baos.write(b);
						}
						fis.close();
						break;
					}
				}else{
					if(file.getName().endsWith(".jar")){// jar file
						JarFile jarFile = new JarFile(new File(url.toURI()));
						ZipEntry zentry = jarFile.getEntry(pathName+".class");
						if(zentry ==null){
							continue;
						}else{
							InputStream is = jarFile.getInputStream(zentry);
							baos = new ByteArrayOutputStream();
							int b = 0;
							while((b=is.read())>=0){
								baos.write(b);
							}
							is.close();
							jarFile.close();
							break;
						}
					}else if(file.getName().endsWith(".zip")){// zip file
						ZipFile zipFile = new ZipFile(new File(url.toURI()));
						ZipEntry zentry = zipFile.getEntry(pathName);
						if(zentry ==null){
							continue;
						}else{
							InputStream is = zipFile.getInputStream(zentry);
							baos = new ByteArrayOutputStream();
							int b = 0;
							while((b=is.read())>=0){
								baos.write(b);
							}
							is.close();
							zipFile.close();
							break;
						}
					}else{ // can't process the file type
						continue;
					}
					
				}
			} catch (URISyntaxException e) {
				continue;
			}
		}
		return baos ==null ? null : baos.toByteArray();
	}
}
