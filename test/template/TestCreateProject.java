package template;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nutz.lang.Files;
import org.nutz.template.CreateProjectProcess;

public class TestCreateProject {
	
	String name = "sampleproject";
	@Before
	public void setUp(){
		Files.deleteDir(new File(name));
	}
	@Test
	public void testCreateProjectProcess(){
		new CreateProjectProcess().process(null);
		new CreateProjectProcess().process("");
		new CreateProjectProcess().process(name);
		Assert.assertTrue(new File(name).exists() && new File(name).isDirectory());
		Assert.assertTrue(new File(name+"/build").exists() && new File(name+"/build").isDirectory());
		Assert.assertTrue(new File(name+"/webapp").exists() && new File(name+"/webapp").isDirectory());
	}
	@After
	public void tearDown(){
		Files.deleteDir(new File(name));
	}
}
