package org.nutz.mvc.upload.performance;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Strings;

public class NFieldMeta {

	private boolean isFile;
	private Map<String, String> metas;

	public NFieldMeta(String content) {
		this.metas = new HashMap<String, String>();
		this.parse(content);
	}

	private void parse(String content) {
		String[] strs = Strings.splitIgnoreBlank(content, ";");
		if (strs.length == 3)
			this.isFile = true;
		else if (strs.length == 2)
			this.isFile = false;

		String name = strs[1].substring(6, strs[1].length() - 1);
		metas.put("name", name);
	}

	public boolean isFile() {
		return isFile;
	}

	public String getName() {
		return metas.get("name");
	}

	public static void main(String[] args) {
		// Content-Disposition: form-data; name="fileData";
		// filename="C:\Users\Amos\Desktop\a.txt"
		String str = "Content-Disposition: form-data; name=\"fileData\"; filename=\"C:\\Users\\Amos\\Desktop\\a.txt\"";
		System.out.println("str:" + str);
		NFieldMeta nfm = new NFieldMeta(str);
		System.out.println("[Name]:" + nfm.getName() + "[isFile]:" + nfm.isFile());
		
		
		
	}

}
