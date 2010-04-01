package org.nutz.mvc.upload;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.Lang;

/**
 * before call method "read()", please call "prepareForRead()" first just once
 */
public class MultipartBody {

	private String contentType;
	private String boundry;
	private List<MultiPlainContent> plainContents = new LinkedList<MultiPlainContent>();
	private List<MultiFileContent> fileContents = new LinkedList<MultiFileContent>();

	// for read
	private int cursor = 0;
	private ArrayList<MultiReadable> mrs = null;
	private int mrsSize = 0;
	private MultiReadable currentMR = null;

	public MultipartBody(String contentType, String boundry) {
		this.contentType = contentType;
		this.boundry = boundry;
	}

	public boolean prepareForRead() {
		this.getMultiReadables();
		mrsSize = mrs.size();
		if (mrsSize <= 0)
			Lang.makeThrow("Need not test, because the request is empty.");
		currentMR = mrs.get(cursor);
		return true;
	}

	public long getContentLength() {
		if (mrs == null) {
			mrs = this.getMultiReadables();
		}
		long length = 0;
		for (MultiReadable mr : mrs) {
			length += mr.length();
		}
		return length;
	}

	public int read() throws Exception {
		int re = currentMR.read();
		if (re == -1) {
			currentMR = mrs.get(cursor++);
			// 全都遍历完，返回-1,结束。
			if (cursor >= mrsSize)
				return -1;
			return this.read();
		} else
			return re;
	}

	public ArrayList<MultiReadable> getMultiReadables() {
		mrs = new ArrayList<MultiReadable>();
		int index = 0;
		for (MultiPlainContent mpc : plainContents) {
			mrs.add(index++, new MultiSeparator());
			mrs.add(index++, mpc);
		}
		for (MultiFileContent mfc : fileContents) {
			mrs.add(index++, new MultiSeparator());
			mrs.add(index++, new MultiFileHead(mfc.getName(), mfc.getFile()
					.getAbsolutePath()));
			mrs.add(index++, mfc);
		}
		mrs.add(index++, new MultiEnd());
		return mrs;
	}

	public void addMultiContent(MultiPlainContent content) {
		plainContents.add(content);
	}

	public void addMultiFileContent(MultiFileContent content) {
		fileContents.add(content);
	}

	public List<MultiPlainContent> getPlainContents() {
		return plainContents;
	}

	public List<MultiFileContent> getFileContents() {
		return fileContents;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getBoundry() {
		return boundry;
	}

	public void setBoundry(String boundry) {
		this.boundry = boundry;
	}

}
