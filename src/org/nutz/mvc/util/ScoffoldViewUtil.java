package org.nutz.mvc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.view.ScoffoldVmView;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ScoffoldViewUtil {
	private static final Logger log = Logger.getLogger(ScoffoldViewUtil.class);
	private static Configuration cfg = null;
	static {
		cfg = new Configuration();
		cfg.setLocale(Locale.CHINA);
		cfg.setDefaultEncoding(Encoding.UTF8);
		cfg.setOutputEncoding(Encoding.UTF8);
		FileTemplateLoader ftl0 = null;
		TemplateLoader tl0 = null;
		ClassTemplateLoader ctl0 = null;
		String appRoot = Mvcs.getNutConfig().getAppRoot();
		try {
			ftl0 = new FileTemplateLoader(new File(appRoot,
					"WEB-INF/svmtemplate/"));
			ctl0 = new ClassTemplateLoader(ScoffoldVmView.class, "resources/");
			tl0 = new ScoffoldTemplateLoader();
		} catch (IOException e) {
			cfg = null;
			log.error(e);
		}
		MultiTemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[] {
				ftl0, ctl0, tl0 });
		cfg.setTemplateLoader(mtl);
	}
	public static String process(String viewName,Object obj) throws IOException, TemplateException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		process(viewName, obj, out);
		return out.toString();
	}
	public static void process(String viewName, Object obj, OutputStream out) throws IOException, TemplateException {
		Template t = cfg.getTemplate(viewName.replace(".", "/") + ".vm");
		Map<String, Object> rootMap = new HashMap<String, Object>();
		if (obj != null) {

			if (obj instanceof Context) {
				Context ctt = (Context) obj;
				for (String key : ctt.keys()) {
					rootMap.put(key, ctt.get(key));
				}
			} else {
				rootMap.put("obj", obj);
			}

		}
		HttpServletRequest req = Mvcs.getReq();
		String message = (String)req.getAttribute("message");
		if(! Strings.isEmpty(message)){
			rootMap.put("message", message);
		}
		rootMap.put("msg", Mvcs.getMessages(Mvcs.getReq()));
		rootMap.put("req", Mvcs.getReq());
		rootMap.put("resp", Mvcs.getResp());
		rootMap.put("base", Mvcs.getReq().getContextPath());
		t.process(rootMap, new OutputStreamWriter(out, Encoding.UTF8));
	}

	// 加载web自定义的模板
	static class ScoffoldTemplateLoader implements TemplateLoader {

		@Override
		public Object findTemplateSource(String viewName) throws IOException {
			if (viewName.endsWith("_zh_CN.vm") || viewName.endsWith("_zh.vm"))
				return null;
			else
				return viewName;
		}

		@Override
		public Reader getReader(Object vn, String charset) throws IOException {
			String viewName = vn.toString();
			String[] tempName = viewName.substring(6, viewName.length()).split(
					"/");
			if (tempName.length != 2)
				throw new FileNotFoundException("视图名称格式不对");
			String domainName = Strings.capitalize(tempName[0]);
			String templateName = tempName[1];
			Template t = cfg.getTemplate(templateName);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				t.process(getRootMap(domainName), new OutputStreamWriter(baos,
						charset));
			} catch (TemplateException e) {
				log.info(e);
				throw new FileNotFoundException("模板解析不对");
			} catch (ClassNotFoundException e1) {
				log.info(e1);
				new FileNotFoundException("类加载器的类不存在");
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(
					baos.toByteArray());
			log.debug(baos.toString());
			return new InputStreamReader(bais);
		}

		@Override
		public long getLastModified(Object vn) {
			return System.currentTimeMillis(); // 这个策略不好，每次都会重新render
			// return 0; // 应该加个开关控制 状态
		}

		@Override
		public void closeTemplateSource(Object arg0) throws IOException {
		}

		private Map<String, Object> getRootMap(String domainName)
				throws ClassNotFoundException {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("domain_name", domainName);
			model.put("low_domain_name", Strings.lowerFirst(domainName));
			List<Map<String, Object>> properties = new ArrayList<Map<String, Object>>();
			Class<?> domainClazz = Class.forName("domains." + domainName);
			if (domainClazz == null)
				throw new ClassNotFoundException(domainName
						+ " class not found");
			if (domainClazz.getAnnotation(Table.class) == null) {
				throw new ClassNotFoundException(
						"this class is not a Table class");
			}
			Field[] fields = domainClazz.getDeclaredFields();
			for (Field f : fields) {
				if (f.getAnnotation(Id.class) != null
						|| f.getAnnotation(Name.class) != null
						|| f.getAnnotation(Column.class) != null) {
					Map<String, Object> pp = new HashMap<String, Object>();
					pp.put("name", f.getName());
					pp.put("type", "string");
					properties.add(pp);
				}
			}
			model.put("properties", properties);
			return model;
		}
	}
}
