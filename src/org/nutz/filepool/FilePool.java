package org.nutz.filepool;

import java.io.File;
import java.io.IOException;

public interface FilePool {

	boolean hasFile(int fId, String suffix);

	int current();

	File removeFile(int fId, String suffix);

	File moveFile(int id, String suffix, File target) throws IOException;

	File createFile(int id, String suffix) throws IOException;

	File createFile(String suffix) throws IOException;

	File renameSuffix(int id, String suffix, String newSuffix);

	int getFileId(File f);

	void empty() throws IOException;

}
