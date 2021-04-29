package org.nutz.mvc;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@FunctionalInterface
public interface CommandLineRunner {

    /**
     * Callback used to run the bean.
     * @throws Exception on error
     */
    void run() throws Exception;

}
