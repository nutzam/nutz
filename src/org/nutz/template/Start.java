package org.nutz.template;

import org.nutz.lang.Strings;

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
				CmdProcess processor = loadProcess(farg);
				if(args.length >1 && processor !=null){
					processor.process(args[1]);
				}else{
					System.out.println("Invalid args!");
				}
			}
		}else
			printHelp("all");
	}
	public static void printHelp(String name){
		if("all".equals(name)){
			System.out.println("命令介绍:\ncreate-project,create-domain,create-controller ,generate-controller,generate-views");
		}else{
			CmdProcess processor = loadProcess(name);
			if(processor !=null){
				System.out.println(processor.getInfo());
			}else{
				System.out.println("Invalid args");
			}
		}
	}
	private static CmdProcess loadProcess(String name){
		if(Strings.isEmpty(name)) return null;
		if(name.indexOf("-") <=0 || name.indexOf("-")>=(name.length()-1)) return null;
		String[] cmds = name.split("-");
		StringBuffer sb = new StringBuffer();
		for(String c : cmds){
			sb.append(Strings.capitalize(c));
		}
		sb.append("Process");
		try {
			Class<?> clazz = CmdProcess.class.getClassLoader().loadClass("org.nutz.template."+sb.toString());
			Object obj = clazz.newInstance();
			if(obj instanceof CmdProcess){
				return (CmdProcess) obj;
			}else
				return null;
			
		} catch (ClassNotFoundException e) {
			return null;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}
}

