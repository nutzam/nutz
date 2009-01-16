package com.zzh.algo.compare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * <b>LineComparator class:</b> <hr color=grey size=1>
 * 
 * Can be used to compare by lines from a <code>InputStream</code>
 * 
 * @author thao created @ 2006
 * 
 */
public class LineComparator implements IComparator {
	private String[] lines;

	/**
	 * @param is
	 *            the input stream used to compare.
	 * @param encoding
	 * @throws IOException
	 */
	public LineComparator(InputStream is, String encoding) throws IOException {
		InputStreamReader isr;
		if (encoding == null) {
			isr = new InputStreamReader(is);
		} else {
			isr = new InputStreamReader(is, encoding);
		}
		BufferedReader br = new BufferedReader(isr);
		String line;
		ArrayList<String> ar = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			ar.add(line);
		}
		lines = (String[]) ar.toArray(new String[ar.size()]);
	}

	public LineComparator(InputStream is) throws IOException {
		this(is, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ihelpuoo.util.algorithm.compare.IComparator#getCount()
	 */
	public int getCount() {
		return lines.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ihelpuoo.util.algorithm.compare.IComparator#getItem(int)
	 */
	public Object getItem(int index) {
		return lines[index];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ihelpuoo.util.algorithm.compare.IComparator#itemEqual(int,
	 * org.ihelpuoo.util.algorithm.compare.IComparator, int)
	 */
	public boolean itemEqual(int index, IComparator other, int otherIndex) {
		String s1 = lines[index];
		Object s2 = other.getItem(otherIndex);
		return s1.equals(s2);
	}
}
