package org.nutz.mvc;

/**
 * 扩展原有 {@link HttpAdaptor} 的能力，
 *
 * @author MingzFan(Mingz.Fan@gmail.com)
 */
public interface HttpAdaptor2 extends HttpAdaptor {

    /**
     * 这是更高级的初始化方法，可以获得更多的初始化信息
     *
     * @see org.nutz.mvc.HttpAdaptor
     */
    void init(ActionInfo ai);

}
