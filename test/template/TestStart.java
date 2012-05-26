package template;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nutz.lang.Files;
import org.nutz.template.Start;

public class TestStart {

	@Before
	public void setUp(){
		new File("src/domains/").mkdirs();
		new File("src/controllers/").mkdirs();
		new File("webapp/WEB-INF/views/").mkdirs();
	}
	@Test
	public void testMain(){
		String[] args = new String[]{"help"};
		Start.main(args);
		args = new String[]{"help","create-domain"};
		Start.main(args);
		args = new String[]{"create-domain","hello"};
		Start.main(args);
		Assert.assertTrue(new File("src/domains/Hello.java").exists());
		
		args = new String[]{"create-controller","hello"};
		Start.main(args);
		Assert.assertTrue(new File("src/controllers/HelloController.java").exists());
		
		args = new String[]{"generate-controller","hello2"};
		Start.main(args);
		Assert.assertTrue(new File("src/controllers/Hello2Controller.java").exists());
		
		args = new String[]{"generate-views","hello"};
		Start.main(args);
		Assert.assertTrue(new File("webapp/WEB-INF/views/hello/show.jsp").exists());
		Assert.assertTrue(new File("webapp/WEB-INF/views/hello/list.jsp").exists());
		Assert.assertTrue(new File("webapp/WEB-INF/views/hello/edit.jsp").exists());
		Assert.assertTrue(new File("webapp/WEB-INF/views/hello/create.jsp").exists());
	}
	@After
	public void tearDown(){
		Files.deleteDir(new File("src/domains/"));
		Files.deleteDir(new File("src/controllers/"));
		Files.deleteDir(new File("webapp/"));
		
	}
}
