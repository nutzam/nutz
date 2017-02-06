package org.nutz.lang;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import org.nutz.img.Colors;
import org.nutz.img.Images;
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
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = environment.getAvailableFontFamilyNames(); // 获得系统字体
        for (String fnm : fonts) {
            fontMap.addv(fnm, true);
        }
    }

    public static void main(String[] args) {
        // BufferedImage avImage = createAvatar("武佩文", 300, "#F00", "#080",
        // null);
        BufferedImage t1 = createAvatar("武佩文");
        File av1 = Files.createFileIfNoExists2("/Users/pw/Downloads/a1.png");
        Images.write(t1, av1);

        BufferedImage t2 = createAvatar("胖五");
        File av2 = Files.createFileIfNoExists2("/Users/pw/Downloads/a2.png");
        Images.write(t2, av2);
    }

    /**
     * 根据名字生成头像，英文采用第一个字母，中文2个字使用2个字，超过2个字采用第一个字
     * 
     * @param name
     *            名字
     * @return
     */
    public static BufferedImage createAvatar(String name) {
        return createAvatar(name, 0, null, null, null, 0, 1);
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
        Font cFont = getFont(fontName, fontStyle, fontSize);
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
