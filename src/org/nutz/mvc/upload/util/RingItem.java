package org.nutz.mvc.upload.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class RingItem {

    byte[] buffer;

    int max;
    /**
     * 左标记，DUMP 时包含
     */
    int l;
    /**
     * 右标记，DUMP 时不包含
     */
    int r;
    /**
     * 下一次 Mark 是开始的位置
     */
    int nextmark;

    RingItem next;

    boolean isLoaded;
    boolean isStreamEnd;

    RingItem(int width) {
        this.buffer = new byte[width];
        this.next = this;
    }

    RingItem createNext() {
        RingItem ri = new RingItem(buffer.length);
        ri.next = next;
        next = ri;
        return ri;
    }

    void load(InputStream ins) throws IOException {
        if (isLoaded) {
            throw new ReloadLoadedRingItemException();
        }
        int bufferSize = buffer.length;
        max = ins.read(buffer, 0, bufferSize);

        // 流里不在有内容了
        if (max < 0) {
            max = 0;
            isStreamEnd = true;
        }
        // 没有读全，继续读，直至read方法返回 -1, 或者读满.
        else {
            while (max < bufferSize) {
                int re = ins.read(buffer, max, bufferSize - max);
                if (re == -1) {
                    isStreamEnd = true;
                    break;
                }
                max += re;
            }
        }

        l = 0;
        r = 0;
        nextmark = 0;
        isLoaded = true;
    }

    void dump(OutputStream ops) throws IOException {
        if (l < r) {
            ops.write(buffer, l, r - l);
        }
        l = nextmark;
        r = l;
        isLoaded = (l < max);
    }

    /**
     * 试图从缓冲开头匹配，如果匹配失败，移动 'r' 并返回 false<br>
     * 如果匹配成功，则移动 'l'（匹配的内容不需要读取） 并返回 true
     * <p>
     * 这个函数，在 BufferRing 发现当前的环节点返回 '>0' 时，需要调用 next 的这个函数，看看是不是可以完整被匹配
     * 
     * @param bs
     *            数组
     * @param offs
     *            偏移量
     * @return 本节点开头是否匹配剩余的部分
     */
    boolean matchHeadingWithRemain(byte[] bs, int offs) {
        int i = 0;
        for (; offs < bs.length; offs++) {
            if (buffer[i++] != bs[offs]) {
                r = i;
                return false;
            }
        }
        // Matched, skip it
        l = i;
        r = i;
        nextmark = i;
        return true;
    }

    boolean isDone4Mark() {
        return nextmark == max;
    }

    /**
     * 从给定 offs 尽力匹配给出的数组。
     * <p>
     * 需要注意的是，如果返回的是 >0 的数，内部的标志位将被设置到第一个匹配字符，以便 DUMP 内容。 <br>
     * 所以，如果下一个节点给出的结论是 -1，但是 'l' 并不是0，那么说明这个匹配是失败的，需要将 本节点的 r 置到 max 处。
     * <p>
     * 返回值
     * <ul>
     * <li><b>-1</b> - 全部被匹配
     * <li><b>0</b> - 未发现匹配
     * <li><b>大于 0</b> - 在缓冲的末尾发现匹配，但是没有匹配全，希望下一个节点继续从这个位置匹配
     * </ul>
     * 
     * @param bs
     *            数组
     * @return -1, 0 或者 +n
     */
    int mark(byte[] bs, int[] fails) {
        if (!isLoaded)
            throw new MarkUnloadedRingItemException();

        byte start = bs[0];

        for (; r < max; r++) {
            // 可能是边界，开始匹配
            if (buffer[r] == start) {
                int re = 0; // 已经匹配长度
                int j = r; // 在内容值字节数组中的指针
                while (true) {
                    re++;
                    j++;
                    // 全部匹配
                    if (re == bs.length) {
                        nextmark = j;
                        return -1;
                    }
                    // 到达本项目的结尾，但是并不确定是否是边界，因为还未匹配完
                    // 因此暂时假设这个不会被匹配
                    if (j == max) {
                        nextmark = max;
                        if (isStreamEnd) {
                            r = max;
                            return 0;
                        }
                        return re;
                    }
                    // 如果字符不相等，那么查看一下回退数组
                    // 如果回退到 0，则退出循环，因为这不是边界，否则继续循环匹配边界
                    if (bs[re] != buffer[j]) {
                        re = fails[re];
                        // 再次判断回退后位置
                        if (bs[re] != buffer[j]) {
                            break;
                        }
                        // 否则扩大边界，并继续循环
                        else {
                            r += re == 0 ? 1 : re;
                        }

                    }
                }
                // make 'r' jump to 'j'
                r = j;
            }
        }
        // Fail to found
        nextmark = max;
        return 0;
    }
}
