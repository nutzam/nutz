package org.nutz.template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Strings;
import org.nutz.template.util.TextParse;

/**
 * 生成domain, domain_name 首字母只能为字母
 * @author tt
 *
 */
public class CreateDomainProcess implements CmdProcess{

	@Override
	public void process(String args) {
		if(Strings.isEmpty(args)){
			System.out.println("args Invalid");
			return ;
		}else{
			try {
				OutputStream out = new FileOutputStream(new File("src/domains/"+Strings.capitalize(args)+".java"));
				TextParse.parse("Domain.vm", generateModel(args),out);
				System.out.println("create domain <" + args + "> success");
			} catch (Exception e) {
				System.out.println("occured some errors ,sorry\nerror code:"+e.getMessage());
			}
		}
	}
	private Map<String,String> generateModel(String name){
		Map<String,String> model = new HashMap<String, String>();
		model.put("domain_name", Strings.capitalize(name));
		model.put("low_domain_name", Strings.lowerFirst(name));
		return model;
	}
	public String getInfo(){
		return "create domain only generate domain contains id,name columns";
	}
}
