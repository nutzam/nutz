package org.nutz.img;

import static java.lang.Integer.parseInt;
import static org.nutz.lang.Strings.dup;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Strings;

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
     * @see #as(String)
     * 
     * @deprecated
     */
    @Deprecated
    public static Color fromString(String str) {
        return as(str);
    }

    /**
     * 将字符串变成颜色值
     * 
     * @param str
     *            颜色字符串，详细，请参看本类的总体描述，如果为空，则表示黑色
     * @return 颜色对象
     */
    public static Color as(String str) {
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

        // // RGBA值: rgba(6,6,6,255)
        // p =
        // Pattern.compile("^RGBA\\s*[(]\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*[)]$");
        // m = p.matcher(str);
        // if (m.find()) {
        // return new Color(parseInt(m.group(1), 10),
        // parseInt(m.group(2), 10),
        // parseInt(m.group(3), 10),
        // parseInt(m.group(4), 10));
        // }

        // RGBA值: rgba(6,6,6,0.9)
        p = Pattern.compile("^RGBA\\s*[(]\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*((\\d[.])?\\d+)\\s*[)]$");
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

    /**
     * 获取图片指定像素点的RGB值
     * 
     * @param srcIm
     *            源图片
     * @param x
     *            横坐标
     * @param y
     *            纵坐标
     * @return RGB值数组
     */
    public static int[] getRGB(BufferedImage srcIm, int x, int y) {
        int pixel = srcIm.getRGB(x, y);
        return getRGB(pixel);
    }

    /**
     * 获取像素点的RGB值(三元素数组)）
     * 
     * @param pixel
     *            像素RGB值
     * @return RGB值数组
     */
    public static int[] getRGB(int pixel) {
        int r = (pixel >> 16) & 0xff;
        int g = (pixel >> 8) & 0xff;
        int b = pixel & 0xff;
        return new int[]{r, g, b};
    }

    public static int getAlpha(int pixel) {
        ColorModel cm = ColorModel.getRGBdefault();
        return cm.getAlpha(pixel);
    }

    /**
     * 获取图片指定像素点的亮度值(YUV中的Y)
     * 
     * @param srcIm
     *            源图片
     * @param x
     *            横坐标
     * @param y
     *            纵坐标
     * @return 亮度值
     */
    public static int getLuminance(BufferedImage srcIm, int x, int y) {
        int[] rgb = getRGB(srcIm, x, y);
        return (int) (0.3 * rgb[0] + 0.59 * rgb[1] + 0.11 * rgb[2]); // 加权法
    }

    /**
     * 获取图片指定像素点的亮度值(YUV中的Y) 类型为double
     * 
     * @param srcIm
     *            源图片
     * @param x
     *            横坐标
     * @param y
     *            纵坐标
     * @return 亮度值
     */
    public static double getLuminanceDouble(BufferedImage srcIm, int x, int y) {
        int[] rgb = getRGB(srcIm, x, y);
        return 0.3 * rgb[0] + 0.59 * rgb[1] + 0.11 * rgb[2]; // 加权法
    }

    /**
     * 获取图片指定像素点的灰度值
     * 
     * @param srcIm
     *            源图片
     * @param x
     *            横坐标
     * @param y
     *            纵坐标
     * @return 灰度值
     */
    public static int getGray(BufferedImage srcIm, int x, int y) {
        int grayValue = getLuminance(srcIm, x, y);
        int newPixel = 0;
        newPixel = (grayValue << 16) & 0x00ff0000 | (newPixel & 0xff00ffff);
        newPixel = (grayValue << 8) & 0x0000ff00 | (newPixel & 0xffff00ff);
        newPixel = (grayValue) & 0x000000ff | (newPixel & 0xffffff00);
        return newPixel;
    }

    /**
     * 获取两个像素点正片叠底后的像素值
     * 
     * @param pixel1
     *            像素点1
     * @param pixel2
     *            像素点1
     * @return 新像素点值
     */
    public static int getMultiply(int pixel1, int pixel2) {
        int[] rgb1 = getRGB(pixel1);
        int[] rgb2 = getRGB(pixel2);
        int r = rgb1[0] * rgb2[0] / 255;
        int g = rgb1[1] * rgb2[1] / 255;
        int b = rgb1[2] * rgb2[2] / 255;
        return new Color(r, g, b).getRGB();
    }

    private Colors() {}
}
