package org.nutz.template;
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
		
	}

}
