package org.nutz.lang.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import org.nutz.lang.Lang;

public class StreamBuffer extends InputStream {

	private static class OutputStreamBuffer extends OutputStream {
		private ArrayList<int[]> bytes = new ArrayList<int[]>();
		private int width = 1024;
		private int index = 0;
		private int cursor = 0;

		@Override
		public void write(int b) throws IOException {
			if (cursor >= width)
				index++;
			int[] row = bytes.size() > index ? bytes.get(index) : null;
			if (null == row) {
				row = new int[width];
				bytes.add(row);
				cursor = 0;
			}
			row[cursor++] = b;
		}

		private int size() {
			return index > 0 ? width * (index - 1) + cursor : cursor;
		}

	}

	private OutputStreamBuffer buffer = new OutputStreamBuffer();
	private int index = 0;
	private int cursor = 0;

	public OutputStream getBuffer() {
		return buffer;
	}

	public void write(int b) throws IOException {
		buffer.write(b);
	}

	@Override
	public int read() throws IOException {
		if (cursor > buffer.width) {
			index++;
			cursor = 0;
		}
		if (index > buffer.index)
			return -1;
		int[] cs = buffer.bytes.get(index);
		if (cursor < buffer.cursor)
			return cs[cursor++];
		return -1;
	}

	@Override
	public int available() throws IOException {
		return buffer.size();
	}

	@Override
	public synchronized void reset() throws IOException {
		index = 0;
		cursor = 0;
	}

	@Override
	public String toString() {
		try {
			return toString("UTF-8");
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public String toString(String charset) throws IOException {
		index = 0;
		cursor = 0;
		InputStreamReader reader = new InputStreamReader(this, charset);
		StringBuilder sb = new StringBuilder();
		int c;
		while ((c = reader.read()) != -1)
			sb.append((char) c);
		return sb.toString();
	}

}
