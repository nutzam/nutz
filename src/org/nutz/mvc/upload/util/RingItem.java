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
		if (max == -1)
			isStreamEnd = true;
	}

	void dump(OutputStream ops) throws IOException {
		if (l < r) {
			ops.write(buffer, l, r - l);
			l = nextmark;
		}
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
	int mark(byte[] bs) {
		if (!isLoaded)
			throw new MarkUnloadedRingItemException();

		byte start = bs[0];

		for (; r < max; r++) {
			if (buffer[r] == start) {
				int re = 0;
				int j = r;
				while (re < bs.length) {
					if (bs[re++] != buffer[j++])
						break;
					if (j >= max)
						break;
				}
				// Found
				if (re == bs.length) {
					nextmark = j;
					return -1;
				}
				// Found partly at the end buffer
				else if (j == max) {
					nextmark = max;
					return isStreamEnd ? 0 : re;
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
