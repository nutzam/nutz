package org.nutz.template;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.nutz.lang.Files;
import org.nutz.lang.Strings;

/**
 * 生成项目:
 * 1,当前文件夹下不能存在与要创建的项目同名的文件夹，如果存在提醒是否删除
 * 2,创建src/controllers, src/domains, src/services,src/utils,src/filters
 * 3,创建resource/dao.js,log4j.properties
 * 4,创建webapp/img,js,css/   webapp/WEB-INF/classes,lib,views,templates, c.tld,fmt.tld,fn.tld
 * 5,将以上项目打包为zip包，然后直接解压到当前目录即可
 * @author tt
 *
 */
public class CreateProjectProcess implements CmdProcess{

	@Override
	public void process(String args) {
		if(Strings.isEmpty(args)){
			System.out.println("args Invalid");
			return ;
		}else{
			try {
				File currentDir = new File(args);
				if(currentDir.exists()){
					System.out.println("please remove the file or folder named <"+args+"> and then retry");
					return ;
				}
				currentDir.mkdirs();
				ZipFile zipFile = new ZipFile(new File(Start.class.getResource("/org/nutz/template/resources/sample.zip").toURI()));
				@SuppressWarnings("unchecked")
				Enumeration<ZipEntry> entries =   (Enumeration<ZipEntry>) zipFile.entries();
				while(entries.hasMoreElements()){
					ZipEntry entry = entries.nextElement();
					if(entry.isDirectory()){
						new File(currentDir.getAbsoluteFile(),entry.getName()).mkdirs();
					}else{
						File target = new File(currentDir.getAbsolutePath(),entry.getName());
						Files.createNewFile(target);
						FileOutputStream fos = new FileOutputStream(target);
						BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
						byte[] buffer = new byte[1024];
						int len = 0;
						while((len = bis.read(buffer)) != -1){
							fos.write(buffer, 0, len);
						}
						bis.close();
						fos.close();
					}
				}
				System.out.println("create project <" + args + "> success");
			} catch (Exception e) {
				System.out.println("occured some errors ,sorry\nerror code:"+e.getMessage());
			}
		}
	}

	@Override
	public String getInfo() {
		return "create project will generate complete project";
	}

}
