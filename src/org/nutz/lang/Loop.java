package org.nutz.lang;

public interface Loop<T> extends Each<T> {

    /**
     * 循环开始的调用
     * 
     * @return true 开始循环，false 不进行循环
     * @throws LoopException
     */
    boolean begin() throws LoopException;

    /**
     * 循环结束得调用
     * 
     * @throws LoopException
     */
    void end() throws LoopException;

}
