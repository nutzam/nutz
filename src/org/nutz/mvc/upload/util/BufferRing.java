package org.nutz.mvc.upload.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.nutz.lang.Lang;

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
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class BufferRing {

	private static void assertRingLength(int len) {
		if (len < 2)
			throw Lang.makeThrow("BufferRing length can not less than 2");
	}

	private InputStream ins;
	private RingItem item;
	public int readed;

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

	public MarkMode mark(byte[] cs) throws IOException {
		int re = item.mark(cs, 0);
		while (re > 0) {
			if (re == -1)
				return MarkMode.FOUND;
			if (re == 0)
				return MarkMode.NOT_FOUND;

			// Partly found
			if (!item.next.isLoaded)
				item.next.load(ins);
			// Mark next Item
			re = item.mark(cs, re);
			// re-decide current one's right
			
			
			// TODO if next item still return >0, it will case error
		}

		if (re == -1)
			return MarkMode.FOUND;
		// re ==0
		return MarkMode.NOT_FOUND;
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
	}

	/**
	 * 从当前节点的 next 开始，依次将所有可用的节点全部加载满
	 * 
	 * @throws IOException
	 */
	public void load() throws IOException {
		RingItem ri = item.next;
		while (!ri.isLoaded)
			ri.load(ins);
	}

}
