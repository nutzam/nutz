package org.nutz.mvc.upload.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class RingItem {

	private byte[] buffer;

	private int max;
	/**
	 * 左标记，DUMP 时包含
	 */
	int l;
	/**
	 * 右标记，DUMP 时不包含
	 */
	private int r;
	/**
	 * 下一次 Mark 是开始的位置
	 */
	private int nextmark;

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
		max = ins.read(buffer, 0, buffer.length);
		l = 0;
		r = 0;
		nextmark = 0;
		isLoaded = true;
		if (max < buffer.length || max == -1)
			isStreamEnd = true;
	}

	boolean canDump() {
		return isLoaded && r > l;
	}

	void dump(OutputStream ops) throws IOException {
		if (l < r) {
			ops.write(buffer, l, r - l);
			l = nextmark;
			// If had dumped all bytes, set the 'isLoaded' to true to accept new
			// bytes
			if (l >= max) {
				isLoaded = false;
			}
		}
	}

	/**
	 * 从给定 offs 尽力匹配给出的数组。
	 * <p>
	 * 需要注意的是，如果返回的是 >0 的数，内部的标志位将被设置到第一个匹配字符，以便 DUMP 内容。 <br>
	 * 所以，如果下一个节点给出的结论是 -1，但是 'l' 并不是0，那么说明这个匹配是失败的，需要将 本节点的 r 置到 max 处。
	 * <p>
	 * 返回值
	 * <ul>
	 * <li>-1 - 全部被匹配
	 * <li>0 - 未发现匹配
	 * <li>大于 0 - 在缓冲的末尾发现匹配，但是没有匹配全，希望下一个节点继续从这个位置匹配
	 * </ul>
	 * 
	 * @param cs
	 *            数组
	 * @param offs
	 *            数组起始点
	 * @return -1, 0 或者 +n
	 */
	int mark(byte[] cs, int offs) {
		if (!isLoaded)
			throw new MarkUnloadedRingItemException();

		byte start = cs[offs];

		for (; r < max; r++) {
			if (buffer[r] == start) {
				int re = offs;
				int j = r;
				for (; re < cs.length; re++) {
					if (cs[re] != buffer[j])
						break;
					j++;
				}
				// Found
				if (re == cs.length) {
					nextmark = j;
					return -1;
				}
				// Found partly at the end buffer
				else if ((j + 1) == max) {
					nextmark = max;
					return re;
				}
				// make 'r' jump to 'j'
				r = j;
			}
		}
		// Fail to found
		return 0;
	}
}
