package org.nutz.runner;

/**
 * @Author: Haimming
 * @Date: 2020-01-16 14:16
 * @Version 1.0
 */
public class TestRunner extends NutRunner {
    /**
     * 新建一个启动器
     *
     * @param rname 本启动器的名称
     */
    public TestRunner(String rname) {
        super(rname);
    }

    public long exec() throws Exception{
        // do something
        System.out.println("do something");
        getLock().stop();
        return 0;
    }

}
