/**
 * Nutz内部使用的日志适配器实现
 * <p>
 * 自动适配 Log4j  和 JDK 的 Log， 并提供了稍微友好一些的日志打印接口。
 * <p>
 * 个人认为，JDK 的 log 很恶心，我通常就是用 Log4j， 这个包也是 Nutz 编译时唯一需要依赖外部 Jar 包的部分
 * (Log4j)，当然，运行时，由于采用了 Nutz 插件机制，它如果发现 Log4j 不可用，它就会转用 JDK 的 Log，
 * 如果 JDK 的 Log 你没有设置而不可用，它就会用控制台输出，总之会帮你把 Log 打印出来
 */
package org.nutz.log;