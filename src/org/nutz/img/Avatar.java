package org.nutz.img;

import java.awt.image.BufferedImage;

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
 * @deprecated 代码已转移到Images类中，请修改引用
 */
@Deprecated
public abstract class Avatar {

    /**
     * 根据名字生成头像，英文采用第一个字母，中文2个字使用2个字，超过2个字采用第一个字
     * 
     * @param name
     *            名字
     * @return 头像
     */
    @Deprecated
    public static BufferedImage createAvatar(String name) {
        return Images.createAvatar(name);
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
     * @param fontName
     *            字体名称 需运行环境中已有该字体名称
     * @param fontSize
     *            字体大小
     * @param fontStyle
     *            字体样式 Font.PLAIN || Font.BOLD || Font.ITALIC
     * @return 头像
     */
    public static BufferedImage createAvatar(String name,
                                             int size,
                                             String fontColor,
                                             String bgColor,
                                             String fontName,
                                             int fontSize,
                                             int fontStyle) {
        return Images.createAvatar(name, size, fontColor, bgColor, fontName, fontSize, fontStyle);
    }

}
