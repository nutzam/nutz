package org.nutz.mvc.init;

import org.junit.Test;
import org.nutz.mvc.AbstractMvcTest;
import org.nutz.mvc.LoadingException;
import org.nutz.mvc.annotation.BlankAtException;
import org.nutz.mvc.init.errmodule.ErrorCatchMainModule;

public class MvcErrorCatchTest {

    private void _mvc(final Class<?> mainModuleType) throws Throwable {
        try {
            (new AbstractMvcTest() {
                protected void initServletConfig() {
                    servletConfig.addInitParameter("modules", mainModuleType.getName());
                }
            }).init();
        }
        catch (LoadingException e) {
            throw e.getCause();
        }
        catch (Exception e) {
            throw e;
        }
    }

    @Test(expected = BlankAtException.class)
    public void testTwoNullAt() throws Throwable {
        _mvc((Class<?>) ErrorCatchMainModule.class);
    }

}
