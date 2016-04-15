package org.nutz.lang.util;

import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author hxy
 */
public class CronSequenceGeneratorTests {

    public CronSequenceGeneratorTests() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    @SuppressWarnings("deprecation")
    @Test
    public void testAt50Seconds() {
        assertEquals(new Date(2012, 6, 2, 1, 0),
                new CronSequenceGenerator("*/15 * 1-4 * * *").next(new Date(2012, 6, 1, 9, 53, 50)));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testAt0Seconds() {
        assertEquals(new Date(2012, 6, 2, 1, 0),
                new CronSequenceGenerator("*/15 * 1-4 * * *").next(new Date(2012, 6, 1, 9, 53)));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testAt0Minutes() {
        assertEquals(new Date(2012, 6, 2, 1, 0),
                new CronSequenceGenerator("0 */2 1-4 * * *").next(new Date(2012, 6, 1, 9, 0)));
    }

    @SuppressWarnings("deprecation")
    @Test(expected = IllegalArgumentException.class)
    public void testWith0Increment() {
        new CronSequenceGenerator("*/0 * * * * *").next(new Date(2012, 6, 1, 9, 0));
    }

    @SuppressWarnings("deprecation")
    @Test(expected = IllegalArgumentException.class)
    public void testWithNegativeIncrement() {
        new CronSequenceGenerator("*/-1 * * * * *").next(new Date(2012, 6, 1, 9, 0));
    }
}
