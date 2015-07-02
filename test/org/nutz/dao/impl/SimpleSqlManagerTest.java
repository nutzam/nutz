package org.nutz.dao.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SimpleSqlManagerTest extends Assert {

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void testSimpleSqlManager() {
        new FileSqlManager("org/nutz");
    }

}
