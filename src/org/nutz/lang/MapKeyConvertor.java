package org.nutz.lang;

/**
 * Lang.convertMapKey 的回调
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface MapKeyConvertor {

    /**
     * @param key
     *            原始的 key
     * @return 新的 key
     */
    String convertKey(String key);

}
