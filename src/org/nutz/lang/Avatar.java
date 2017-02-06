package org.nutz.lang;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.nutz.img.Colors;
import org.nutz.lang.util.NutMap;

/**
 * 头像生成工具
 * 
 * @author pw
 *
 */
public abstract class Avatar {

    private static NutMap fontMap = NutMap.NEW();

    static {
        String[] fonts = OS.fontsRefresh(); // 获得系统字体
        for (String fnm : fonts) {
            fontMap.addv(fnm, true);
        }
    }

    /**
     * 根据名字生成头像，英文采用第一个字母，中文2个字使用2个字，超过2个字采用第一个字
     * 
     * @param name
     *            名字
     * @return
     */
    public static BufferedImage createAvatar(String name) {
        return createAvatar(name, 0, null, null, null, 0, Font.BOLD);
    }

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
     *            字体名称 需运行环境中已有该字体名称
     * @param fontSize
     *            字体大小
     * @param fontStyle
     *            字体样式 Font.PLAIN || Font.BOLD || Font.ITALIC
     * @return
     */
    public static BufferedImage createAvatar(String name,
                                             int size,
                                             String fontColor,
                                             String bgColor,
                                             String fontName,
                                             int fontSize,
                                             int fontStyle) {
        // 检查输入项目
        if (Strings.isBlank(name)) {
            return null;
        }
        if (size <= 0) {
            size = 256;
        }
        if (Strings.isBlank(fontColor)) {
            fontColor = "#FFF";
        }
        if (Strings.isBlank(bgColor)) {
            bgColor = "#000";
        }
        if (fontSize <= 0) {
            fontSize = size / 2;
        }
        if (fontStyle < 0 || fontStyle > 2) {
            fontStyle = Font.BOLD;
        }
        // 分析要写入的文字
        String content = name;
        if (name.length() > 2) {
            content = ("" + name.charAt(0));
        }
        content = content.toUpperCase();
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
        Font cFont = getFont(fontName, fontStyle, fontSize);
        gc.setColor(colorFont);
        gc.setFont(cFont);
        FontMetrics cFontM = gc.getFontMetrics(cFont);
        int cW = cFontM.stringWidth(content);
        int ascent = cFontM.getAscent(); // 取得Ascent
        int descent = cFontM.getDescent(); // 取得Descent
        int x, y;
        x = size / 2 - cW / 2;
        y = (size - (ascent + descent)) / 2 + ascent;
        gc.drawString(content, x, y);
        return im;
    }

    private static Font getFont(String name, int style, int size) {
        if (Strings.isBlank(name)) {
            // 尝试微软雅黑，黑体，宋体等常见字体
            String[] commonFonts = new String[]{"Microsoft YaHei",
                                                "Hei",
                                                "Microsoft Sans Serif",
                                                "Courier New",
                                                "Courier",
                                                "Monaco"};
            Font ff = findFont(commonFonts, style, size);
            if (ff == null) {
                throw new RuntimeException("Please manually set the font, or add some common fonts in the system");
            }
            return ff;
        }
        return new Font(name, style, size);
    }

    private static Font findFont(String[] fnames, int style, int size) {
        for (String font : fnames) {
            if (fontMap.getBoolean(font, false)) {
                return new Font(font, style, size);
            }
        }
        return null;
    }
}
