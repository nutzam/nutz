package org.nutz.log;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.log.impl.SystemLogAdapter;

public class SystemLogAdapterTest {

    @Test
    public void testGetLogger() {
        LogAdapter logAdapter = new SystemLogAdapter();
        assertNotNull(logAdapter.getLogger(Log.class.getName()));
    }

    @Test
    public void testCanWork() {
        assertTrue(new SystemLogAdapter().canWork());
    }

}
