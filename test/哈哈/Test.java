package 哈哈;

import java.lang.reflect.Method;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader2Test;
import org.nutz.mvc.impl.LoadingsImplTest;
@RunWith(Suite.class)
@Suite.SuiteClasses({LoadingsImplTest.class,AnnotationIocLoader2Test.class})
public class Test {

	public static void main(String[] args) {
		testMethod();
	}
	public static void testStrings(){
		System.out.println(String.class.getName());
		System.out.println(String.class.getPackage().getName());
	}
	public static void testMethod(){
		Method[] methods = String.class.getMethods();
		System.out.println(methods[0].getName());
		System.out.println(methods[0].getDeclaringClass());
		System.out.println(methods[0].getClass());
	}
}
