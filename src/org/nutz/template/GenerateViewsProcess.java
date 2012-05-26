package org.nutz.template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Strings;
import org.nutz.template.util.ClassUtil;
import org.nutz.template.util.TextParse;

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
public class GenerateViewsProcess implements CmdProcess{

	@Override
	public void process(String args) {
		if(Strings.isEmpty(args)){
			System.out.println("args Invalid");
			return ;
		}else{
			try {
				String viewPath = "webapp/WEB-INF/views/"+Strings.lowerFirst(args)+"/";
				new File(viewPath).mkdirs();
				OutputStream out = new FileOutputStream(new File(viewPath+"show.jsp"));
				TextParse.parse("show.vm", generateModel(args),out);
				System.out.println("generate  <" + args + "/show.jsp> success");
				
				out = new FileOutputStream(new File(viewPath+"create.jsp"));
				TextParse.parse("create.vm", generateModel(args),out);
				System.out.println("generate  <" + args + "/create.jsp> success");
				
				out = new FileOutputStream(new File(viewPath+"edit.jsp"));
				TextParse.parse("edit.vm", generateModel(args),out);
				System.out.println("generate  <" + args + "/edit.jsp> success");
				
				out = new FileOutputStream(new File(viewPath+"list.jsp"));
				TextParse.parse("list.vm", generateModel(args),out);
				System.out.println("generate  <" + args + "/list.jsp> success");
				
			} catch (Exception e) {
				System.out.println("occured some errors ,sorry\nerror code:"+e.getMessage());
			}
		}
	}
	private Map<String,Object> generateModel(String name) throws IOException, ClassNotFoundException{
		Map<String,Object> model = new HashMap<String, Object>();
		model.put("domain_name", Strings.capitalize(name));
		model.put("low_domain_name", Strings.lowerFirst(name));
		List<Map<String,Object>> properties = new ArrayList<Map<String,Object>>();
		Class<?> domainClazz = ClassUtil.findClass("domains."+Strings.capitalize(name));
		if(domainClazz == null) throw new ClassNotFoundException(name+" class not found");
		if(domainClazz.getAnnotation(Table.class) ==null){
			throw new ClassNotFoundException("this class is not a Table class");
		}
		Field[] fields = domainClazz.getDeclaredFields();
		for(Field f : fields ){
			if(f.getAnnotation(Id.class) != null || f.getAnnotation(Name.class) != null || f.getAnnotation(Column.class) != null){
				Map<String, Object> pp = new HashMap<String, Object>();
				pp.put("name", f.getName());
				pp.put("type","string");
				properties.add(pp);
			}
		}
		model.put("properties", properties);
		return model;
	}
	@Override
	public String getInfo() {
		return "generate views will create domain_name/show.jsp,edit.jsp.list,jsp,create.jsp in WEB-INF/views folder";
	}
}
