package org.nutz.template;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
			ZipInputStream zis = null;
			try {
				File currentDir = new File(args);
				if(currentDir.exists()){
					System.out.println("please remove the file or folder named <"+args+"> and then retry");
					return ;
				}
				currentDir.mkdirs();
				
				InputStream is = Start.class.getResourceAsStream("/org/nutz/template/resources/sample.zip");
				zis = new ZipInputStream(is);
				ZipEntry entry = null;
				while((entry = zis.getNextEntry()) != null){
					if(entry.isDirectory()){
						new File(currentDir.getAbsoluteFile(),entry.getName()).mkdirs();
					}else{
						FileOutputStream fos = null;
						BufferedInputStream bis = null;
						try{
							File target = new File(currentDir.getAbsolutePath(),entry.getName());
							Files.createNewFile(target);
							fos = new FileOutputStream(target);
							bis = new BufferedInputStream(zis);
							byte[] buffer = new byte[1024];
							int len = 0;
							while((len = bis.read(buffer)) != -1){
								fos.write(buffer, 0, len);
							}
						}finally{
							if(fos != null)
								fos.close();
						}
					}
					zis.closeEntry();
				}
				System.out.println("create project <" + args + "> success");
			} catch (Exception e) {
				System.out.println("occured some errors ,sorry\nerror code:"+e.getMessage());
			}finally{
				if(zis != null)
					try {zis.close();} catch (IOException e) {}
			}
		}
	}

	@Override
	public String getInfo() {
		return "create project will generate complete project";
	}

}
