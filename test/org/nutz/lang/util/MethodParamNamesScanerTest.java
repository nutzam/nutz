package org.nutz.lang.util;

import java.util.List;

import org.junit.Test;
import org.nutz.resource.Scans;

public class MethodParamNamesScanerTest {

    @Test
    public void testGetParamNames() throws Throwable {
        List<Class<?>> list = Scans.me().scanPackage("org.nutz");
        for (Class<?> klass : list) {
            MethodParamNamesScaner.getParamNames(klass) ;
        }
    }

}
