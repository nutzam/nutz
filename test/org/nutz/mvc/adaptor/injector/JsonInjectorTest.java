package org.nutz.mvc.adaptor.injector;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Heewon Lee (pingpingy03@gmail.com)
 */
public class JsonInjectorTest {

    @Test
    public void test_null_refer() {
        // 准备数据
        JsonInjector inj = new JsonInjector(null, "");

        // 检测
        Throwable exc = assertThrows(NullPointerException.class, () -> {
            inj.get(null, null, null, null);
        });
        assertEquals("refer", exc.getMessage());
    }

}
