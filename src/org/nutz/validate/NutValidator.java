package org.nutz.validate;

public interface NutValidator {

    /**
     * @param val
     *            待检测的值
     * @return 修改过的值，如果没有修改则会返回原值
     * @throws NutValidateException
     *             出错后用这个异常汇报具体错误细节
     */
    Object check(Object val) throws NutValidateException;

    /**
     * 返回本检查器的优先级，越小越优先检查
     * 
     * <ul>
     * <li><code>trim</code> : 0
     * <li><code>notNull</code> : 1
     * <li><code>minLength</code> : 11
     * <li><code>maxLength</code> : 12
     * <li><code>其他</code> : > 100
     * </ul>
     * 
     * @return 本检查器的优先级
     */
    int order();

}
