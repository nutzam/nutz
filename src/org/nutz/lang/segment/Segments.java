package org.nutz.lang.segment;

import java.io.File;

import org.nutz.lang.Files;
import org.nutz.lang.util.Context;

/**
 * 代码片段的帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Segments {

    /**
     * 根据一个对象填充所有片段的占位符
     * 
     * @param seg
     *            片段
     * @param obj
     *            对象
     * @return 填充后的片段对象
     */
    public static Segment fill(Segment seg, Object obj) {
        if (null == obj || null == seg)
            return seg;
        return seg.setBy(obj);
    }

    /**
     * 根据一个文件生成一个代码片段
     * 
     * @param f
     *            文件
     * @return 片段对象
     */
    public static Segment read(File f) {
        String txt = Files.read(f);
        return new CharSegment(txt);
    }

    /**
     * 根据字符串片段，将上下文对象替换对应占位符。未赋值的占位符维持原样
     * <p>
     * 比如：
     * 
     * @param seg
     *            片段对象
     * @param context
     *            上下文对象
     * @return 替换后的字符串
     */
    public static String replace(Segment seg, Context context) {
        if (null == seg)
            return null;

        // 增加缺失的占位符号
        for (String key : seg.keys())
            if (!context.has(key))
                context.set(key, "${" + key + "}");

        return seg.render(context).toString();
    }

    /**
     * 根据字符串片段，将上下文对象替换对应占位符。未赋值的占位符维持原样
     * 
     * @param pattern
     *            字符串片段
     * @param context
     *            上下文对象
     * @return 替换后的字符串
     */
    public static String replace(String pattern, Context context) {
        if (null == context)
            return pattern;
        return replace(new CharSegment(pattern), context);
    }

    /**
     * 根据一段字符串生成一个代码片段
     * 
     * @param str
     *            字符串
     * @return 片段对象
     */
    public static Segment create(String str) {
        return new CharSegment(str);
    }
}
