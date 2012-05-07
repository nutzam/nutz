package org.nutz.template;

import java.util.HashMap;
import java.util.Map;

/**
 * domainName 小写即可，生成时会将首字母大写
 * create-project projectName
 * create-domain domainName
 * create-controller domainName
 * create-view domainName
 * create-all domainName
 * help
 * @author tt
 *
 */
public class Start {
	public static Map<String, String> helpSimpleInfos;
	public static Map<String, String> helpInfos;
	public static Map<String,CmdProcess> processors;
	static {
		helpSimpleInfos = new HashMap<String, String>();
		helpInfos = new HashMap<String, String>();
		processors = new HashMap<String, CmdProcess>();
		
		helpInfos.put("help", "help <command> ,list all the commands or more infomation for specific command");
		helpInfos.put("create-project", "create-project projectName, create the project in the current folder");
		helpInfos.put("create-domain", "create-domain domainName, create the domain in the package domains. , also you can create the domain-class directly");
		helpInfos.put("create-controller", "create-controller domainName, create the controller for the domain in package controllers, it has index,show,list,search,create,save,edit,update .. methods");
		helpInfos.put("create-view", "create-controller domainName, create the views for the domain in folder /WEB-INF/views/domainName/, it has list,search ,create,edit,show .. jsps");
		helpInfos.put("create-all", "create-all domainName, create all the controller and the views");
	}
	public static void main(String[] args) {
		if(args.length >0){
			String farg = args[0];
			if("help".equals(farg)){
				if(args.length ==1){
					printHelp("all");
				}else{
					printHelp(args[1]);
				}
			}else{
				if(args.length >1 && processors.containsKey(farg)){
					processors.get(farg).process(args[1]);
				}else{
					System.out.println("Invalid args!");
				}
			}
		}else
			printHelp("all");
	}
	public static void printHelp(String name){
		if("all".equals(name)){
			System.out.println("命令介绍:");
			for(String arg : helpSimpleInfos.keySet()){
				System.out.printf("%s\t\t%s", arg,helpSimpleInfos.get(arg));
				System.out.println();
			}
		}else{
			if(helpSimpleInfos.containsKey(name)){
				System.out.printf("%s\t\t%s",name,helpInfos.get(name));
				System.out.println();
			}else{
				System.out.println("Invalid args");
			}
		}
		
	}
}

