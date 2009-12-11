package org.nutz.lang.util;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * 读字节码的帮助类
 * <p>
 * 思路是缓存一个 int[] 数组，根据游标，读取不同的数据
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ByteReading {

	private int[] bytes;
	private int cursor;
	private IntRange range;
	private int mark;

	public ByteReading(int[] bytes) {
		if (null == bytes)
			throw Lang.makeThrow("Byte array Can not be null");
		this.bytes = bytes;
		this.cursor = 0;
		this.range = IntRange.make(0, bytes.length - 1);
	}

	/**
	 * 向后移动n个字节
	 * 
	 * @param step
	 *            移动多少个字节
	 * @return ByteReading 移动成功，null，已经越界
	 */
	public ByteReading next(int step) {
		cursor += step;
		checkCursor();
		return isOutOfRange() ? null : this;
	}

	private void checkCursor() {
		if (cursor > bytes.length)
			cursor = bytes.length;
		else if (cursor < 0)
			cursor = 0;
	}

	/**
	 * 向后移动 n个字节
	 * 
	 * @param step
	 *            移动多少个字节
	 * @return ByteReading 移动成功，null，已经越界
	 */
	public ByteReading prev(int step) {
		cursor -= step;
		checkCursor();
		return isOutOfRange() ? null : this;
	}

	/**
	 * @return 游标是否越界
	 */
	public boolean isOutOfRange() {
		return range.gt(cursor) || range.lt(cursor);
	}

	/**
	 * @return 游标位置
	 */
	public int cursor() {
		return cursor;
	}

	/**
	 * @return 字节数组长度
	 */
	public int size() {
		return bytes.length;
	}

	/**
	 * 标记某一个索引
	 * 
	 * @param index
	 *            索引值
	 * @return ByteReading
	 */
	public ByteReading setMark(int index) {
		mark = index;
		return this;
	}

	/**
	 * 标记当前游标
	 * 
	 * @return ByteReading
	 */
	public ByteReading mark() {
		return setMark(cursor);
	}

	/**
	 * @return 当前的标记
	 */
	public int getMark() {
		return mark;
	}

	/**
	 * 标记归零
	 * 
	 * @return ByteReading
	 */
	public ByteReading clearMark() {
		mark = 0;
		return this;
	}

	/**
	 * 根据游标，获取一个字节组成的整数，比如
	 * 
	 * <pre>
	 * [34] [00] [0F] [35]
	 *             ----^ 游标
	 * </pre>
	 * 
	 * 将返回整数 0F， 即 15
	 * <p>
	 * 本函数不改变游标
	 * 
	 * @return 整数
	 */
	public int getInt() {
		if (cursor < 1)
			throw new CursorException("read int2", cursor, bytes.length);

		return (bytes[cursor - 1] & 0xFF);
	}

	/**
	 * 根据游标，获取一个两个字节组成的整数，比如
	 * 
	 * <pre>
	 * [34] [00] [0F] [35]
	 *             ----^ 游标
	 * </pre>
	 * 
	 * 将返回整数 000F， 即 15
	 * <p>
	 * 本函数不改变游标
	 * 
	 * @return 整数
	 */
	public int getInt2() {
		if (cursor < 2)
			throw new CursorException("read int2", cursor, bytes.length);

		int low = (bytes[cursor - 1] & 0xFF);
		int high = (bytes[cursor - 2] & 0xFF);
		return low | (high << 8);
	}

	/**
	 * 根据游标，获取一个四个字节组成的整数，比如
	 * 
	 * <pre>
	 * [12] [34] [00] [00] [00] [0F] [35]
	 *                            ----^ 游标
	 * </pre>
	 * 
	 * 将返回整数 00 00 00 0F， 即 15
	 * <p>
	 * 本函数不改变游标
	 * 
	 * @return 整数
	 */
	public int getInt4() {
		if (cursor < 4)
			throw new CursorException("read int4", cursor, bytes.length);

		int low = (bytes[cursor - 1] & 0xFF);
		int lowh = (bytes[cursor - 2] & 0xFF);
		int higl = (bytes[cursor - 3] & 0xFF);
		int high = (bytes[cursor - 4] & 0xFF);
		return low | (lowh << 8) | (higl << 16) | (high << 24);
	}

	/**
	 * 将从某一个字节一直读到游标所在字节（不包括游标所在字节），并将内容转换成 UTF-8 字符串
	 * 
	 * <pre>
	 * [12] [34] [00] [41] [42] [43] [35]
	 *                            ----^ 游标 = 5
	 * </pre>
	 * 
	 * 如果 getUtf8(3) 将返回 41 42 43， 即 "ABC"
	 * 
	 * <p>
	 * 更多详情，请参看 <a href=
	 * "http://java.sun.com/javase/6/docs/api/java/io/DataInput.html#modified-utf-8"
	 * > 变长 UTF-8 字符串</a>
	 * 
	 * @param from
	 *            起始字节下标
	 * @return Utf8字符串
	 */
	public String getUtf8(int from) {
		if (from < 0)
			from = 0;

		// TODO zzh: 这个实现有问题，需要参看文档，做适当的修改
		StringBuilder sb = new StringBuilder();
		for (int i = from; i < cursor; i++)
			sb.append((char) bytes[i]);
		return sb.toString();
	}

	/**
	 * @return 从标记下标到游标之间的字节组成的字符串
	 */
	public String getUtf8() {
		return getUtf8(mark);
	}

	/**
	 * @return 从标记位到游标之间的字节数组（包括标记位，不包括游标）
	 */
	public int[] getBytes() {
		int[] re = new int[cursor - mark + 1];
		for (int i = 0; i < cursor - mark; i++) {
			re[i] = bytes[i + mark];
		}
		return re;
	}

	/**
	 * 从标记位到当前的游标获得打印字符串以便调试。
	 * 
	 * @param width
	 *            字符串一行有多少字节
	 * @return 打印字符串。如果没有字节可以打印，返回 "<!empty>"
	 */
	public String toHexString(int width) {
		if (this.isOutOfRange()) {
			return "<!empty>";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < (cursor - mark); i++) {
			if (i > 0)
				if (i % width == 0)
					sb.append('\n');
			sb.append(Strings.toHex(bytes[i + mark], 2)).append(' ');
		}
		return sb.toString();
	}

}
