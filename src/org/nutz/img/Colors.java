package org.nutz.img;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Strings;

import static org.nutz.lang.Strings.dup;
import static java.lang.Integer.parseInt;

/**
 * 提供快捷的解析颜色值的方法
 * 
 * 颜色值的字符串类型支持如下:
 * <ul>
 * <li>RGB: #FFF
 * <li>RRGGBB: #F0F0F0
 * <li>ARGB: #9FE5
 * <li>AARRGGBB: #88FF8899
 * <li>RGB值: rgb(255,33,89)
 * <li>RGBA值: rgba(6,6,6,0.8)
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public final class Colors {

    /**
     * 将字符串变成颜色值
     * 
     * @param str
     *            颜色字符串，详细，请参看本类的总体描述，如果为空，则表示黑色
     * @return 颜色对象
     */
    public static Color fromString(String str) {
        if (null == str)
            return Color.BLACK;

        // 整理一下字符串以便后面匹配分析
        str = Strings.trim(str.toUpperCase());

        if (str.startsWith("#")) {
            str = str.substring(1);
        }

        if (str.endsWith(";"))
            str = str.substring(0, str.length() - 1);

        // RGB: #FFF
        Pattern p = Pattern.compile("^([0-9A-F])([0-9A-F])([0-9A-F])$");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return new Color(parseInt(dup(m.group(1), 2), 16),
                             parseInt(dup(m.group(2), 2), 16),
                             parseInt(dup(m.group(3), 2), 16));
        }

        // RRGGBB: #F0F0F0
        p = Pattern.compile("^([0-9A-F]{2})([0-9A-F]{2})([0-9A-F]{2})$");
        m = p.matcher(str);
        if (m.find()) {
            return new Color(parseInt(m.group(1), 16),
                             parseInt(m.group(2), 16),
                             parseInt(m.group(3), 16));
        }

        // ARGB: #9FE5
        p = Pattern.compile("^([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])$");
        m = p.matcher(str);
        if (m.find()) {
            return new Color(parseInt(dup(m.group(2), 2), 16),
                             parseInt(dup(m.group(3), 2), 16),
                             parseInt(dup(m.group(4), 2), 16),
                             parseInt(dup(m.group(1), 2), 16));
        }

        // AARRGGBB: #88FF8899
        p = Pattern.compile("^([0-9A-F]{2})([0-9A-F]{2})([0-9A-F]{2})([0-9A-F]{2})$");
        m = p.matcher(str);
        if (m.find()) {
            return new Color(parseInt(m.group(2), 16),
                             parseInt(m.group(3), 16),
                             parseInt(m.group(4), 16),
                             parseInt(m.group(1), 16));
        }

        // RGB值: rgb(255,33,89)
        p = Pattern.compile("^RGB\\s*[(]\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*[)]$");
        m = p.matcher(str);
        if (m.find()) {
            return new Color(parseInt(m.group(1), 10),
                             parseInt(m.group(2), 10),
                             parseInt(m.group(3), 10));
        }

        // RGBA值: rgba(6,6,6,255)
        p = Pattern.compile("^RGBA\\s*[(]\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*[)]$");
        m = p.matcher(str);
        if (m.find()) {
            return new Color(parseInt(m.group(1), 10),
                             parseInt(m.group(2), 10),
                             parseInt(m.group(3), 10),
                             parseInt(m.group(4), 10));
        }

        // RGBA值: rgba(6,6,6,0.9)
        p = Pattern.compile("^RGBA\\s*[(]\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(0?[.][0-9]+)\\s*[)]$");
        m = p.matcher(str);
        if (m.find()) {
            float alpha = Float.parseFloat(m.group(4));

            return new Color(parseInt(m.group(1), 10),
                             parseInt(m.group(2), 10),
                             parseInt(m.group(3), 10),
                             (int) (255.0f * alpha));
        }

        // 全都匹配不上，返回黑色
        return Color.BLACK;

    }

    private Colors() {}
}
