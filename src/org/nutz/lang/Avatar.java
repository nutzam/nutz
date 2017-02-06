package org.nutz.lang;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.nutz.img.Colors;

/**
 * 头像生成工具
 * 
 * @author pw
 *
 */
public abstract class Avatar {

    /**
     * 根据名字生成头像，英文采用第一个字母，中文2个字使用2个字，超过2个字采用第一个字
     * 
     * @param name
     *            名字
     * @param size
     *            图片大小，默认256
     * @param fontColor
     *            文字颜色 默认白色
     * @param bgColor
     *            背景颜色 默认黑色
     * @param font
     *            字体 (可选)
     * @return
     */
    public static BufferedImage createAvatar(String name,
                                             int size,
                                             String fontColor,
                                             String bgColor,
                                             String fontName) {
        // 检查输入项目
        if (Strings.isBlank(name)) {
            return null;
        }
        if (size <= 0) {
            size = 256;
        }
        if (Strings.isBlank(fontColor)) {
            fontColor = "#FFFFFF";
        }
        if (Strings.isBlank(bgColor)) {
            bgColor = "#000000";
        }
        int fontSize = size / 10;
        // 分析要写入的文字
        String content = ("" + name.charAt(0)).toUpperCase();
        // 准备参数
        BufferedImage im;
        Graphics2D gc;
        Color colorFont = Colors.as(fontColor);
        Color colorBg = Colors.as(bgColor);
        // 生成背景
        im = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        gc = im.createGraphics();
        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gc.setBackground(colorBg);
        gc.clearRect(0, 0, size, size);
        // 写入文字
        Font cFont = getFont(fontName, Font.BOLD, fontSize);
        gc.setColor(colorFont);
        gc.setFont(cFont);
        FontMetrics cFontM = gc.getFontMetrics(cFont);
        int cW = cFontM.stringWidth(content);
        int cH = cFontM.getHeight();
        int cHFix = cH / 3 * 2;
        int x, y;
        x = size / 2 - cW / 2;
        y = cHFix + (size / 2 - cH / 2) + (cFontM.getAscent() - fontSize);
        gc.drawString(content, x, y);
        return im;
    }

    private static Font getFont(String name, int style, int size) {
        if (Strings.isBlank(name)) {
            GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fonts = environment.getAvailableFontFamilyNames(); // 获得系统字体
            // 尝试微软雅黑，黑体，宋体
            if (OS.isWindows()) {

            }
            // 尝试
            else if (OS.isLinux()) {

            }
        }
        return new Font(name, style, size);
    }
}
