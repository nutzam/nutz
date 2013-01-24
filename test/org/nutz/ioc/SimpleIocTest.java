package org.nutz.ioc;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.ioc.meta.issue348.DogMaster;

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
}
