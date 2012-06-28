package org.nutz.lang;

/**
 * 匹配类型：
 * 
 * <ul>
 * <li>YES: 匹配
 * <li>LACK: 参数不足
 * <li>NO: 不匹配
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public enum MatchType {
    /**
     * 匹配
     */
    YES,
    /**
     * 参数不足
     */
    LACK,
    /**
     * 不匹配
     */
    NO,
    /**
     * 长度相同，但是需要转换
     */
    NEED_CAST
}
