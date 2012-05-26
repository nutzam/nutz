package template;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses({
	TestCreateProject.class,TestClassLoader.class,TestStart.class,
	TestTextParse.class
})
public class TestAll {

}
