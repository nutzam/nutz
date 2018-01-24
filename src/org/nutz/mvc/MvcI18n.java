package org.nutz.mvc;

import java.text.MessageFormat;
import java.util.Map;

/**
 * web环境下 国际化 相关帮助函数
 *
 * @author 306955302@qq.com
 */
public class MvcI18n {

    /**
     * 取得国际化信息
     *
     * @param key
     * @return
     */
    public static String message(String key) {
        return messageOrDefault(key, "");
    }

    /**
     * 取得国际化信息并格式化
     * {0}帐号登录{1}  ->>  test帐号登录失败
     *
     * @param key
     * @return
     */
    public static String message(String key, Object... params) {
        return MessageFormat.format(messageOrDefault(key, ""), params);
    }

    /**
     * 取得国际化信息并格式化
     * {0}帐号登录{1}  ->>  test帐号登录失败
     *
     * @param key
     * @return
     */
    public static String messageOrDefaultFormat(String key, String defaultValue, Object... params) {
        return MessageFormat.format(messageOrDefault(key, defaultValue), params);
    }

    /**
     * 取得国际化信息
     *
     * @param key
     * @return
     */
    public static String messageOrDefault(String key, String defaultValue) {
        String localizationKey = Mvcs.getLocalizationKey() == null ? Mvcs.getDefaultLocalizationKey() : Mvcs.getLocalizationKey();
        Map<String, Object> localization = Mvcs.getLocaleMessage(localizationKey);
        if (null != localization) {
            Object value = localization.get(key);
            return value == null ? defaultValue : String.valueOf(value);
        }
        return defaultValue;
    }

}

