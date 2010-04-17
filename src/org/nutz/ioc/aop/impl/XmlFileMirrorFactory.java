package org.nutz.ioc.aop.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.nutz.aop.ClassAgent;
import org.nutz.aop.ClassDefiner;
import org.nutz.aop.DefaultClassDefiner;
import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.MethodMatcher;
import org.nutz.aop.MethodMatcherFactory;
import org.nutz.aop.SimpleMethodMatcher;
import org.nutz.aop.asm.AsmClassAgent;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.MirrorFactory;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlFileMirrorFactory implements MirrorFactory {
	
	private List<Mapper> list = new ArrayList<Mapper>();
	
	private Ioc ioc;

	private ClassDefiner cd;
	
	static class Mapper{
		Pattern namePattern;
		MethodMatcher methodMatcher;
		String interceptorName;
		boolean singleton;
	}
	
	public XmlFileMirrorFactory(Ioc ioc,String... fileNames) throws ParserConfigurationException, SAXException, IOException {
		this.ioc = ioc;
		this.cd = new DefaultClassDefiner();
		DocumentBuilder builder = Lang.xmls();
        Document document;
        for (String fileName : fileNames) {
                document = builder.parse(Files.findFile(fileName));
                document.normalizeDocument();
                NodeList nodeListZ = ((Element) document.getDocumentElement()).getElementsByTagName("class");
                for (int i = 0; i < nodeListZ.getLength(); i++)
                     parse((Element)nodeListZ.item(i));
        }

	}

	private void parse(Element item) {
		Mapper mapper = new Mapper();
		mapper.namePattern = Pattern.compile(item.getAttribute("name"));
		mapper.methodMatcher = MethodMatcherFactory.matcher(item.getAttribute("method"));
		mapper.interceptorName = item.getAttribute("interceptor");
		if (item.hasAttribute("singleton"))
			mapper.singleton = Boolean.parseBoolean(item.getAttribute("singleton"));
		list.add(mapper);
	}

	@SuppressWarnings("unchecked")
	public <T> Mirror<T> getMirror(Class<T> type, String name) {
		try {
			return (Mirror<T>) Mirror.me(cd.load(type.getName() + ClassAgent.CLASSNAME_SUFFIX));
		}
		catch (ClassNotFoundException e) {}
		ClassAgent agent = new AsmClassAgent();
		Mirror<T> mirror = Mirror.me(type);
		Method [] methods = mirror.getAllDeclaredMethodsWithoutTop();
		boolean flag = false;
		for (Mapper mapper : list) {
			if (mapper.namePattern.matcher(type.getName()).find()){
				for (Method method : methods) {
					if (MirrorFactoryUtil.canInterceptor(method)){
						if (mapper.methodMatcher.match(method)){
							flag = true;
							agent.addInterceptor(new SimpleMethodMatcher(method), getMethodInterceptor(mapper));
						}
					}
				}
			}
		}
		if (flag)
			return Mirror.me(agent.define(cd, type));
		return mirror;
	}
	
	private HashMap<String, MethodInterceptor> cachedMethodInterceptor = new HashMap<String, MethodInterceptor>();
	
	protected MethodInterceptor getMethodInterceptor(Mapper mapper) {
		if (mapper.interceptorName.startsWith("ioc:")){
			String objName = mapper.interceptorName.substring(4);
			return ioc.get(MethodInterceptor.class, objName);
		}
		try {
			if (mapper.singleton == false)
				return (MethodInterceptor)Class.forName(mapper.interceptorName).newInstance();
			MethodInterceptor methodInterceptor = cachedMethodInterceptor.get(mapper.interceptorName);
			if (methodInterceptor == null){
				methodInterceptor = (MethodInterceptor)Class.forName(mapper.interceptorName).newInstance();
				cachedMethodInterceptor.put(mapper.interceptorName, methodInterceptor);
			}
			return methodInterceptor;
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}
}
