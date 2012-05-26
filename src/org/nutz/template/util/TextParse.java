package org.nutz.template.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TextParse {

	private static Configuration cfg = new Configuration();
	private static boolean initialed = false;
	private static String charsetName = "UTF-8";
	public static void init() {
		// 应该是设置一些常量 cfg.setAutoImports(map)
		cfg.setDateFormat("yyyy-MM-dd HH:mm:ss");
		cfg.setDefaultEncoding(charsetName);
		//cfg.setDirectoryForTemplateLoading(new File(templateDir));
		cfg.setClassForTemplateLoading(org.nutz.template.util.TextParse.class,"/org/nutz/template/resources/"); // for the resources in jar
		// TODO , 这里的template loading应该要自定义类,因为需要从jar中加载
	}
	public static String parse(String fileName,@SuppressWarnings("rawtypes") Map model) throws IOException, TemplateException{
		if(! initialed)
			init();
		Template t = cfg.getTemplate(fileName);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		t.process(model, new OutputStreamWriter(bos,charsetName));
		return bos.toString(charsetName);
	}
	public static void parse(String fileName,@SuppressWarnings("rawtypes") Map map,OutputStream os) throws IOException, TemplateException{
		if(! initialed)
			init();
		Template t = cfg.getTemplate(fileName);
		t.process(map, new OutputStreamWriter(os,charsetName));
	}
}
