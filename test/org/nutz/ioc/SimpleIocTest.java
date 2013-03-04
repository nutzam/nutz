package org.nutz.ioc;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.ioc.meta.issue348.DogMaster;
import org.nutz.ioc.meta.issue399.Issue399Service;

public class SimpleIocTest {

    @Test(expected=IocException.class)
    public void test_error_bean() {
        Ioc ioc = new NutIoc(new AnnotationIocLoader(DogMaster.class.getPackage().getName()));
        try {
            ioc.get(DogMaster.class);
            fail("Never Success");
        }
        catch (IocException e) {}
        ioc.get(DogMaster.class);
    }
    
    @Test
    public void test_no_singleton_depose() {
    	Issue399Service.CreateCount = 0;
    	Issue399Service.DeposeCount = 0;
    	Ioc ioc = new NutIoc(new AnnotationIocLoader(Issue399Service.class.getPackage().getName()));
    	for (int i = 0; i < 100; i++) {
			ioc.get(Issue399Service.class);
		}
    	ioc.depose();
    	System.gc();
    	assertEquals(100, Issue399Service.CreateCount);
    	assertEquals(0, Issue399Service.DeposeCount);
    	
    }
}
