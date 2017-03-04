package org.nutz.img;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.nutz.lang.OS;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

/**
 * 根据文字生成图片的小工具，适用于一些生成头像的场景。
 * 
 * <pre>
 * 
 * 直接生成黑底白字
 * Avatar.createAvatar("王小二");
 * 
 * 手动设置图片大小，文字/背景色，字体，字体样式, 字体大小等
 * Avatar.createAvatar("王小二", 128, "rgba(255,0,0,0.8)", "rgba(0,0,0,0.1)", "微软雅黑", 64, Font.BOLD);
 * 
 * </pre>
 * 
 * @author pw
 *
 */
public abstract class Avatar {

    private static NutMap fontMap = NutMap.NEW();

    private static String[] commonFonts = new String[]{
                                                       // windows
                                                       "微软雅黑",
                                                       "微软正黑体",
                                                       "黑体",
                                                       "宋体",
                                                       "仿宋",
                                                       "新宋体",
                                                       "楷体",
                                                       "仿宋GB2312",
                                                       "楷体GB2312",
                                                       "Microsoft YaHei",
                                                       "Microsoft YaHei UI",
                                                       "Microsoft JhengHei",
                                                       "SimHei",
                                                       "SimSun",
                                                       "FangSong",
                                                       "NSimSun",
                                                       "FangSongGB2312",
                                                       "KaiTiGB2312",
                                                       // macOS
                                                       "冬青黑体",
                                                       "Hiragino Sans GB",
                                                       "STHeiti",
                                                       "STSong",
                                                       "STFangsong",
                                                       "STKait",
                                                       "Apple LiGothic Medium",
                                                       "Apple LiSung Light",
                                                       "LiHei Pro Medium",
                                                       "LiSong Pro Light",
                                                       // ubuntu
                                                       "Dialog",
                                                       "Serif",
                                                       "SansSerif",
                                                       "Monospaced",
                                                       "Lucida Sans Typewriter",
                                                       "DialogInput",
                                                       "Lucida Bright",
                                                       "Lucida Sans",
                                                       // centos
                                                       "Abyssinica SIL",
                                                       "AR PL UMing CN",
                                                       "AR PL UMing HK",
                                                       "AR PL UMing TW",
                                                       "AR PL UMing TW MBE",
                                                       "Bitstream Charter",
                                                       "Caladea",
                                                       "Cantarell",
                                                       "Carlito",
                                                       "Century Schoolbook L",
                                                       "Courier 10 Pitch",
                                                       "Cursor",
                                                       "DejaVu Sans",
                                                       "DejaVu Sans Condensed",
                                                       "DejaVu Sans Light",
                                                       "DejaVu Sans Mono",
                                                       "DejaVu Serif",
                                                       "DejaVu Serif Condensed",
                                                       "Dialog",
                                                       "DialogInput",
                                                       "Dingbats",
                                                       "FreeMono",
                                                       "FreeSans",
                                                       "FreeSerif",
                                                       "Jomolhari",
                                                       "Khmer OS",
                                                       "Khmer OS Content",
                                                       "Khmer OS System",
                                                       "Liberation Mono",
                                                       "Liberation Sans",
                                                       "Liberation Serif",
                                                       "LKLUG",
                                                       "Lohit Assamese",
                                                       "Lohit Bengali",
                                                       "Lohit Devanagari",
                                                       "Lohit Gujarati",
                                                       "Lohit Kannada",
                                                       "Lohit Malayalam",
                                                       "Lohit Marathi",
                                                       "Lohit Nepali",
                                                       "Lohit Oriya",
                                                       "Lohit Punjabi",
                                                       "Lohit Tamil",
                                                       "Lohit Telugu",
                                                       "Lucida Bright",
                                                       "Lucida Sans",
                                                       "Lucida Sans Typewriter",
                                                       "Madan2",
                                                       "Meera",
                                                       "Monospaced",
                                                       "NanumGothic",
                                                       "NanumGothicExtraBold",
                                                       "Nimbus Mono L",
                                                       "Nimbus Roman No9 L",
                                                       "Nimbus Sans L",
                                                       "Nuosu SIL",
                                                       "Open Sans",
                                                       "Open Sans Extrabold",
                                                       "Open Sans Light",
                                                       "Open Sans Semibold",
                                                       "OpenSymbol",
                                                       "Overpass",
                                                       "Padauk",
                                                       "PakType Naskh Basic",
                                                       "PT Sans",
                                                       "PT Sans Narrow",
                                                       "SansSerif",
                                                       "Serif",
                                                       "Standard Symbols L",
                                                       "STIX",
                                                       "URW Bookman L",
                                                       "URW Chancery L",
                                                       "URW Gothic L",
                                                       "URW Palladio L",
                                                       "Utopia",
                                                       "VL Gothic",
                                                       "Waree",
                                                       "WenQuanYi Micro Hei",
                                                       "WenQuanYi Micro Hei Mono",
                                                       "WenQuanYi Zen Hei",
                                                       "WenQuanYi Zen Hei Mono",
                                                       "WenQuanYi Zen Hei Sharp"};

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
        // 判断图片格式
        int imageType = BufferedImage.TYPE_INT_RGB;
        if (colorFont.getAlpha() < 255 || colorBg.getAlpha() < 255) {
            imageType = BufferedImage.TYPE_INT_ARGB;
        }
        // 生成背景
        im = new BufferedImage(size, size, imageType);
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
