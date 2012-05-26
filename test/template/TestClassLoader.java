package template;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.template.util.ClassUtil;

public class TestClassLoader {

	@Test
	public void testLoadClass() throws IOException, ClassNotFoundException{
		ClassUtil.setProjectHome("/home/tt/workspace/java/ttcms/");
		String clazzName = "domains.News";
		Class<?> clazz = ClassUtil.findClass(clazzName);
		Assert.assertEquals(clazzName, clazz.getName());
		
		clazzName = "org.sitemesh.webapp.SiteMeshFilter";
		clazz = ClassUtil.findClass(clazzName);
		Assert.assertEquals(clazzName, clazz.getName());
	}
}
