package org.nutz.mvc.init.module;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.CommandLineRunner;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@IocBean
public class MvcCommandLineRunner implements CommandLineRunner {
    @Override
    public void run() throws Exception {
        System.out.println("call run Class CommandLineRunner");
    }

    @IocBean
    public CommandLineRunner commandLineRunner(){
        return new CommandLineRunner(){
            @Override
            public void run() throws Exception {
                System.out.println("call run Method CommandLineRunner");
            }
        };
    }
}
