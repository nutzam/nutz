package org.nutz.filepool;

import java.io.File;
import java.io.IOException;

public interface FilePool {

	boolean hasFile(long fId, String suffix);

	long current();

	File removeFile(long fId, String suffix);

	File moveFile(long id, String suffix, File target) throws IOException;

	File createFile(long id, String suffix) throws IOException;

	File createFile(String suffix) throws IOException;

	File changeExtension(long id, String suffix, String newSuffix);

	long getFileId(File f);

	void empty() throws IOException;

}
