package template;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.nutz.template.util.TextParse;

import freemarker.template.TemplateException;

public class TestTextParse {

	@Test
	public void testParse() throws IOException, TemplateException, URISyntaxException{
		Map<Object, Object> model = new HashMap<Object, Object>();
		model.put("domain_name", "Hello");
		model.put("low_domain_name", "hello");
		System.out.println(TextParse.parse("Domain.vm", model));
	}
}
