package org.nutz;

/**
 * 用于识别当前版本号和版权声明! <br/>
 * Nutz is Licensed under the Apache License, Version 2.0 (the "License")
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * @author Wendal(wendal1985@gmail.com)
 * 
 */
public final class Nutz {
    
    /**
     * 标示类,没有new的可能性
     */
    Nutz() {}

    /**
     * 获取 Nutz 的版本号，版本号的命名规范
     * 
     * <pre>
     * [大版本号].[质量号].[发布流水号]
     * </pre>
     * 
     * 这里有点说明
     * <ul>
     * <li>大版本号 - 表示 API 的版本，如果没有重大变化，基本上同样的大版本号，使用方式是一致的
     * <li>质量号 - 可能为 a: Alpha内部测试品质, b:Beta 公测品质, r:Release 最终发布版
     * <li>发布流水 - 每次发布增加 1
     * </ul>
     * 
     * @return nutz 项目的版本号
     */
    public static String version() {
        return String.format("%d.%s.%d-SNAPSHOT",
                             majorVersion(),
                             releaseLevel(),
                             minorVersion());
    }

    /**
     * 大版本号
     */
    public static int majorVersion() {
        return 1;
    }

    /**
     * 发布流水
     */
    public static int minorVersion() {
        return 66;
    }

    /**
     * 质量号
     */
    public static String releaseLevel() {
        return "r";
    }
}
