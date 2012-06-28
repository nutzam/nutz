package org.nutz.mvc.upload.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.stream.StringOutputStream;

/**
 * 分页缓冲 - 专为高效的成块的解析 HTTP Multipart 输入流而设计
 * <p>
 * 总体的想法是，在内存中构建一个数组环，每个节点是固定宽度的数组（默认8192）。 <br>
 * 每次翻页 都相当于将当前环节点的下一个节点读满
 * <p>
 * 每个环节点都有指针指向其下一个节点<br>
 * 每个节点都有一个有效结尾位置以及本节点是否为一个输入流的最末节点
 * <p>
 * 支持一个便利的标记方法（不支持回溯，标记的开始和结束只能在一个节点内不能跨节点）<br>
 * 支持成块写到输出流<br>
 * 
 * <p>
 * <b style=color:red>考虑到效率问题，BufferRing 有一些基本假设：</b>
 * </p>
 * <ul>
 * <li>环节点最少为 3 个
 * <li>每个节点的长度，至少要比即将给定的 boundary 要长
 * </ul>
 * 如果违背了上述假设， BufferRing 将发生不可预知的异常
 * 
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class BufferRing {

    private static void assertRingLength(int len) {
        if (len < 2)
            throw Lang.makeThrow("BufferRing length can not less than 2");
    }

    private InputStream ins;
    RingItem item;
    long readed;

    public BufferRing(InputStream ins, int len, int width) {
        assertRingLength(len);
        this.ins = ins;
        /*
         * Make ring
         */
        item = new RingItem(width);
        for (int i = 1; i < len; i++) {
            item = item.createNext();
        }
    }

    public MarkMode mark(RemountBytes rb) throws IOException {
        return mark(rb.bytes, rb.fails);
    }

    /**
     * 根据给定的字节数组，在环中作标记，以便 dump
     * 
     * @param bs
     *            数组
     * @return 标记模式
     * @throws IOException
     */
    private MarkMode mark(byte[] bs, int[] fails) throws IOException {
        RingItem ri = item;
        int re;
        while ((re = ri.mark(bs, fails)) >= 0 && ri.isDone4Mark()) {
            // 结尾匹配 bs 的开始，看不出这是否是一个结束，所以暂时当作
            // 结束标记，并看看下一个节点。如果不是结束，则需要将 r 置到 max
            if (re > 0) {
                // 下一个节点没有加载，加载一下
                if (!ri.next.isLoaded) {
                    ri.next.load(ins);
                    readed += ri.next.max;
                }
                // 如果当前的环节点的 next指向最初节点，说明整个当前的环已经被读满了，
                // 因此所以不能判断这个位置是不是边界
                // 因此做一下记录，下次加载时，本节点应该为头部
                else if (ri.next == this.item) {
                    ri.nextmark = ri.r;
                    return MarkMode.NOT_FOUND;
                }
                // 匹配头部
                if (ri.next.matchHeadingWithRemain(bs, re)) {
                    return MarkMode.FOUND;
                }
                // 没有匹配上，重置当前节点
                else {
                    ri.r = ri.max;
                    ri.nextmark = ri.max;
                }
            }
            if (ri.isStreamEnd)
                break;
            // 指向下一个节点
            ri = ri.next;
            // 保证该节点已经加载了
            if (!ri.isLoaded) {
                ri.load(ins);
                readed += ri.max;
            }
            // 如果已经循环了一圈，退出
            if (ri == item)
                break;
        }
        if (re == -1)
            return MarkMode.FOUND;

        return ri.isStreamEnd ? MarkMode.STREAM_END : MarkMode.NOT_FOUND;
    }

    /**
     * 被 Dump 的节点将会通过标志表示可以再次加载
     * 
     * @param ops
     *            输出流，如果为 null，则不会输出，直接改变标志
     * @throws IOException
     */
    public void dump(OutputStream ops) throws IOException {
        while (item.isLoaded) {
            item.dump(ops);
            // All content had been dumped, move to next
            if (!item.isLoaded)
                item = item.next;
            // Else break the loop and waiting for next 'mark'
            else
                break;
        }
        ops.flush();
    }
    
    /**
     * 将标记的内容 Dump 成一个字符串,使用默认字符集
     * 
     * @return 字符串
     * 
     * @throws IOException
     */
    String dumpAsString() throws IOException {
        StringBuilder sb = new StringBuilder();
        OutputStream ops = new StringOutputStream(sb, Encoding.defaultEncoding());
        dump(ops);
        return sb.toString();
    }

    /**
     * 将标记的内容 Dump 成一个字符串
     * 
     * @return 字符串
     * 
     * @throws IOException
     */
    public String dumpAsString(String charset) throws IOException {
        StringBuilder sb = new StringBuilder();
        OutputStream ops = new StringOutputStream(sb, charset);
        dump(ops);
        return sb.toString();
    }

    /**
     * 不输出，直接跳过 Mark，相当于将当前的 Mark dump 到一个空的输出流
     * 
     * @throws IOException
     */
    public void skipMark() throws IOException {
        dump(new OutputStream() {
            public void write(int b) throws IOException {}

            public void close() throws IOException {}

            public void flush() throws IOException {}

            public void write(byte[] b, int off, int len) throws IOException {}

            public void write(byte[] b) throws IOException {}

        });
    }

    /**
     * 从当前节点的 next 开始，依次将所有可用的节点全部加载满
     * 
     * @return 一共读去的字节数
     * 
     * @throws IOException
     */
    public long load() throws IOException {
        if (item.isStreamEnd)
            return 0;
        RingItem ri = item;
        while (!ri.isLoaded) {
            ri.load(ins);
            if (ri.max > 0)
                readed += ri.max;
            ri = ri.next;
        }
        return readed;
    }

    /**
     * @return 已经读了多少字节
     */
    public long readed() {
        return readed;
    }

    /**
     * 安全关闭输入流
     */
    public void close() {
        Streams.safeClose(ins);
    }

}
