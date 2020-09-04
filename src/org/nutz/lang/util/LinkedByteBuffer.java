package org.nutz.lang.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;

/**
 * 可自动延申的字节数组，内部有两个游标（只读游标cursor）和（只写游标last）
 * <p>
 * 这个类设计给有一小块缓冲数据，可能需要同时读写的场景。
 * <p>
 * !!! 注意，本类非线程安全，如果多线程共享实例，请自行加锁保护。
 * 
 * <pre>
 * 实际上的存储 ...
 * |<---- unit --->|
 * |               | -----
 * [0d 0a 18 23 ...]  ^
 * [35 f2 25 0e ...]  nb
 * [12 ae 11 01 ...]  V
 * ...               <- 如果满了，再写会自动延申
 * 
 * 逻辑上可以看作一个数组
 * 
 *    rIndex（读）                                             capacity 已分配容量
 *      V                            V
 * [0d 0a 18 23 35 f2 25 0e .  .  .  ]
 *              ^            ^
 *           wIndex       limit 有效区的位置
 *            只写下标
 *      如果指向 capacity则无空间
 * </pre>
 * 
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class LinkedByteBuffer {

    // 为了防止内存爆掉，这里指定一个上限吧
    private int maxCapacity;

    /**
     * 已加载的缓冲行字节总数
     */
    private int capacity;

    /**
     * 表示有效区的长度
     */
    private int limit;

    /**
     * 只读游标
     */
    private int rIndex;

    /**
     * 只写游标
     */
    private int wIndex;
    /**
     * 动态增长内存时，需要分配的最小字节单位
     */
    private int unit;

    /**
     * 缓冲（字节数组列表）
     */
    private ArrayList<byte[]> list;

    /**
     * 创建一个 10M上限实例，8K增量单位，默认分配 80K的实例
     */
    public LinkedByteBuffer() {
        this(8192, 10, 1024 * 10240);
    }

    /**
     * 创建一个 10M上限实例，8K增量单位的实例
     * 
     * @param nb
     *            初始分配多少单位的字节
     */
    public LinkedByteBuffer(int nb) {
        this(8192, nb, 1024 * 10240);
    }

    /**
     * 创建一个 10M上限实例
     * 
     * @param unit
     *            动态分配字节的最小单位，譬如 8192(8K)
     * @param nb
     *            初始分配多少单位的字节
     */
    public LinkedByteBuffer(int unit, int nb) {
        this(unit, nb, 1024 * 10240);
    }

    /**
     * @param unit
     *            动态分配字节的最小单位，譬如 8192(8K)
     * @param nb
     *            初始分配多少单位的字节
     * @param maxLimit
     *            最大可分配的字节数，譬如 1024*10240(10M)
     */
    public LinkedByteBuffer(int unit, int nb, int maxLimit) {
        this.unit = unit;
        this.capacity = unit * nb;
        this.limit = 0;
        this.rIndex = 0;
        this.wIndex = 0;

        // 默认上限给 10M
        this.maxCapacity = maxLimit;

        // 分配内存
        list = new ArrayList<byte[]>(nb);
        for (int i = 0; i < nb; i++) {
            list.add(new byte[unit]);
        }
    }

    /**
     * 将只读和只写游标同时归0; 并清空有效区
     */
    public void reset() {
        limit = 0;
        rIndex = 0;
        wIndex = 0;
    }

    /**
     * 从当前位置偏移只读游标。但是它也遵循 {@link #seekRead(int)} 函数的限定。
     * 
     * @param off
     *            偏移量
     * @return 偏移后的只读游标位置
     */
    public int skipRead(int off) {
        return this.seekRead(rIndex + off);
    }

    /**
     * 移动只写游标。你给入的新位置不能小于0，也不能大过有效区上限。
     * <p>
     * 否则，会自动对齐到两端边界。即，小于0会当作0，大于有效区上限则等于上限。
     * 
     * @param pos
     *            新的只读游标位置
     * @return 移动后的只读游标位置
     */
    public int seekRead(int pos) {
        this.rIndex = Math.max(0, Math.min(pos, limit));
        return this.rIndex;
    }

    /**
     * 从当前位置偏移只写游标。但是它也遵循 {@link #seekWrite(int)} 函数的限定。
     * 
     * @param off
     *            偏移量
     * @return 偏移后的只写游标位置
     */
    public int skipWrite(int off) {
        return this.seekWrite(wIndex + off);
    }

    /**
     * 移动只写游标。你给入的新位置不能小于0，也不能大过【有效区】上限。
     * <p>
     * 否则，会自动对齐到两端边界。即，小于0会当作0，大于上限则等于上限。
     * 
     * @param pos
     *            新的只写游标位置
     * @return 移动后的只写游标位置
     */
    public int seekWrite(int pos) {
        this.wIndex = Math.max(0, Math.min(pos, limit));
        return this.wIndex;
    }

    /**
     * 将自身的内容，从当前位置（内部只写游标）写入输入数组的内容
     * 
     * @param buf
     *            输入数组
     * 
     * @throws IOException
     *             当写入的字节超过自己的指定最大限度
     */
    public void write(byte[] buf) throws IOException {
        write(buf, 0, buf.length);
    }

    /**
     * 将自身的内容，从当前位置（内部只写游标）写入输入数组的内容
     * 
     * @param buf
     *            输入数组
     * @param off
     *            要写入字节的起始位置
     * @param len
     *            要写入多少字节
     * 
     * @throws IOException
     *             当写入的字节超过自己的指定最大限度
     */
    public void write(byte[] buf, int off, int len) throws IOException {
        // 超过上限了，不能写了
        if (capacity + len > this.maxCapacity) {
            throw new IOException("Output of MaxLimitation " + this.maxCapacity);
        }

        // 还是有多少空间是可写的？
        int remain = this.capacity - this.wIndex;

        // 超过了，那么还需要分配多少行数据
        if (len > remain) {
            int space = len - remain;
            int n = space / unit;
            if (n * unit < space) {
                n += 1;
            }
            // 分配
            for (int i = 0; i < n; i++) {
                list.add(new byte[unit]);
            }
            capacity = list.size() * unit;
        }

        // 开始点
        int row = wIndex / unit; // 从第几行开始
        int col = wIndex - row * unit; // 从第几列开始

        // 对其的开始（即，如果不从行首 copy，那么对齐到行首，以便计算行数
        int padLen = len + col;

        // 一共 要完整写几行
        int r_count = padLen / unit; // 先需要完整写的行数

        // 准备裸行
        byte[] tag = list.get(row);

        // 写第一行
        int x = off; // 目标数组要 copy 的起始位置
        int n = Math.min(unit - col, len); // 要 copy 的字节数
        int c = 0; // 一共 copy 完的字节数
        System.arraycopy(buf, x, tag, col, n);
        c += n;
        x += n;

        // 写整行
        int i = 1; // 行从 row 偏移的下标
        for (; i < r_count; i++) {
            tag = list.get(row + i);
            System.arraycopy(buf, x, tag, 0, unit);
            c += unit;
            x += unit;
        }

        // 写最后一行
        if (c < len) {
            tag = list.get(row + i);
            n = len - c;
            System.arraycopy(buf, x, tag, 0, n);
        }

        // 最后移动只写游标
        this.wIndex += len;
        this.limit = Math.max(this.limit, this.wIndex);
    }

    /**
     * 写入字符串
     * 
     * @param str
     *            字符串
     * 
     * @throws IOException
     *             当写入的字节超过自己的指定最大限度
     */
    public void write(String str) throws IOException {
        byte[] buf = str.getBytes(Encoding.CHARSET_UTF8);
        this.write(buf);
    }

    /**
     * 写入一行字符串，会自动再后面添加换行符.
     * 
     * @param str
     *            字符串(UTF-8编码)
     * @throws IOException
     *             当写入的字节超过自己的指定最大限度
     */
    public void writeLine(String str) throws IOException {
        byte[] buf = str.getBytes(Encoding.CHARSET_UTF8);
        byte[] nwl = System.lineSeparator().getBytes();
        this.write(buf);
        this.write(nwl);
    }

    /**
     * 将自身的内容，从当前位置（内部游标）copy 到目标数组，并会将游标指向下一个未读取的位置
     * 
     * @param buf
     *            目标数组
     * @return 一共实际 copy 的字节数。 -1 表示不在有字节可以被 copy 了
     * 
     * @see #read(byte[], int, int)
     */
    public int read(byte[] buf) {
        return read(buf, 0, buf.length);
    }

    /**
     * 将自身的内容，从当前位置（内部只读游标）copy 到目标数组，并会将只读游标指向下一个未读取的位置
     * 
     * @param buf
     *            目标数组
     * @param off
     *            起始位置下标
     * @param len
     *            最多 copy 多少字节
     * @return 一共实际 copy 的字节数。 -1 表示不在有字节可以被 copy 了
     */
    public int read(byte[] buf, int off, int len) {
        int remain = limit - rIndex;
        if (remain <= 0) {
            return -1;
        }
        len = Math.min(len, remain);
        if (0 >= len) {
            return len;
        }

        // 开始点
        int row = rIndex / unit; // 从第几行开始
        int col = rIndex - row * unit; // 从第几列开始

        // 对其的开始（即，如果不从行首 copy，那么对齐到行首，以便计算行数
        int padLen = len + col;

        // 一共要完整 copy 几行
        int r_count = padLen / unit; // 先需要 copy 的整行

        // 准备源
        byte[] src = list.get(row);

        // Copy 第一行
        int x = off; // 目标数组要 copy 的起始位置
        int n = Math.min(unit - col, len); // 要 copy 的字节数
        int c = 0; // 一共 copy 完的字节数
        System.arraycopy(src, col, buf, x, n);
        c += n;
        x += n;

        // Copy 其余整行
        int i = 1; // 行从 cursor 偏移的下标
        for (; i < r_count; i++) {
            src = list.get(row + i);
            System.arraycopy(src, 0, buf, x, unit);
            c += unit;
            x += unit;
        }

        // Copy 最后一行
        if (c < len) {
            src = list.get(row + i);
            n = len - c;
            System.arraycopy(src, col, buf, x, n);
        }

        // 最后移动只读游标
        this.rIndex += len;

        // 返回实际读取的字节数
        return len;
    }

    /**
     * 读取一行的字符串(UTF-8编码)
     * 
     * @return 从当前只读位置到遇到的第一个换行符之间的字符串(UTF-8编码)。 <br>
     *         null 表示已经没有可读的内容了
     */
    public String readLine() {
        // 木有行了
        if (rIndex >= limit)
            return null;

        // 开始点
        int row = rIndex / unit; // 从第几行开始
        int col = rIndex - row * unit; // 从第几列开始

        // 最多寻找的字符数
        int max = limit - rIndex;

        // 试图寻找到下一个 '\n'
        int count = 0;
        boolean found = false;
        for (; row < list.size(); row++) {
            byte[] bs = list.get(row);
            for (; col < unit; col++) {
                byte b = bs[col];
                count++; // 计数
                if (b == '\n' || count >= max) {
                    found = true;
                    break;
                }
            }
            if (found)
                break;
            else
                col = 0;
        }

        // 读取字符串
        byte[] buf = new byte[count];
        this.read(buf);

        // 去掉结尾的 -n
        int i = buf.length - 1;
        if (buf[i] == '\n')
            i--;
        if (buf[i] == '\r')
            i--;
        if (buf[i] != '\n')
            i++;

        // 返回字符串
        return new String(buf, 0, i, Encoding.CHARSET_UTF8);
    }

    /**
     * 读取剩下的全部字符串(UTF-8编码)
     * 
     * @return 从当前只读位置到有效区结尾全部的内容，并转成字符串(UTF-8编码)。 <br>
     *         null 表示没有可读的内容了
     */
    public String readAll() {
        int len = limit - rIndex;
        if (len <= 0) {
            return null;
        }
        byte[] buf = new byte[len];
        this.read(buf);

        // 返回字符串
        return new String(buf, Encoding.CHARSET_UTF8);
    }

    /**
     * 从指定位置读取一个字节
     * 
     * @param index
     *            下标。如果为负数，则表示从后面读取
     * @return 字节
     */
    public byte get(int index) {
        if (this.isEmpty()) {
            return -1;
        }
        // 从后面数
        if (index < 0) {
            index = Math.max(0, limit + index);
        }
        // 从前面数
        else {
            index = Math.min(index, limit - 1);
        }
        int row = index / unit; // 从第几行开始
        int col = index - row * unit; // 从第几列开始
        return this.list.get(row)[col];
    }

    /**
     * 向指定从指定位置写入一个字节
     * 
     * @param index
     *            下标。如果为负数，则表示从后面读取
     * @return 字节
     */
    public void set(int index, int b) {
        if (this.isEmpty()) {
            return;
        }
        // 从后面数
        if (index < 0) {
            index = Math.max(0, limit + index);
        }
        // 从前面数
        else {
            index = Math.min(index, limit - 1);
        }
        int row = index / unit; // 从第几行开始
        int col = index - row * unit; // 从第几列开始
        this.list.get(row)[col] = (byte) b;
    }

    /**
     * @return 是否内容空空如也
     */
    public boolean isEmpty() {
        return this.limit <= 0;
    }

    /**
     * @return 本实例能增长的上限。默认为 10M
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * @return 已经分配的可写字节总数
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * @return 有效区大小
     */
    public int getLimit() {
        return limit;
    }

    /**
     * 截取内容。给定长度必须要在有效区长度范围以内
     * 
     * @param limit
     *            长度
     * @return 截取后的内容大小
     */
    public int truncate(int limit) {
        this.limit = Math.min(this.limit, limit);
        return this.limit;
    }

    /**
     * @return 只写游标
     */
    public int getWriteIndex() {
        return wIndex;
    }

    /**
     * @return 只读游标
     */
    public int getReadIndex() {
        return rIndex;
    }

    /**
     * @return 每次增长的最小字节数单位
     */
    public int getUnit() {
        return unit;
    }

    /**
     * @return 内容有效区的 SHA1 签名
     */
    public String sha1sum() {
        try {
            return digest("SHA1");
        }
        catch (NoSuchAlgorithmException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * @return 内容有效区的 MD5 签名
     */
    public String md5sum() {
        try {
            return digest("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * 计算内容有效区的 的数字签名
     *
     * @param algorithm
     *            算法，比如 "SHA1" 或者 "MD5" 等
     * @return 数字签名
     * @throws NoSuchAlgorithmException
     *             数据签名方法不支持
     */
    public String digest(String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);

        // 更新的整行
        int count = limit / unit;
        int row = 0;
        for (; row < count; row++) {
            byte[] buf = this.list.get(row);
            md.update(buf);
        }

        // 更新最后一行
        int n = limit - count * unit;
        if (n > 0) {
            byte[] buf = this.list.get(row);
            md.update(buf, 0, n);
        }

        // 计算签名
        byte[] hashBytes = md.digest();
        return Lang.fixedHexString(hashBytes);

    }

    public byte[] toArray() {
        byte[] re = new byte[limit];
        for (int i = 0; i < list.size(); i++) {
            int from = i * unit;
            int to = Math.min(limit, from + unit);
            if (to <= from) {
                break;
            }
            int len = to - from;
            byte[] src = list.get(i);
            System.arraycopy(src, 0, re, from, len);
        }
        return re;
    }

    public String toString() {
        byte[] bs = this.toArray();
        return new String(bs, Encoding.CHARSET_UTF8);
    }

}
