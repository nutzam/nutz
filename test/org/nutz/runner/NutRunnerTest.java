package org.nutz.runner;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Author: Haimming
 * @Date: 2020-01-16 14:14
 * @Version 1.0
 */
public class NutRunnerTest {

    @Test
    public void run() {
        TestRunner testRunner =new TestRunner("test");
        assertEquals(testRunner.getName(),"test");
        testRunner.setSleepAfterError(3);
        assertTrue(testRunner.isRunning());
        testRunner.run();
        assertEquals(testRunner.getInterval(),1);
        assertTrue(testRunner.isWaiting());
        assertTrue(testRunner.isAlive());
        testRunner.setDebug(true);
        assertTrue(testRunner.isDebug());
        assertNotNull(testRunner.getUpAt());
        assertNotNull(testRunner.getDownAt());


    }

}