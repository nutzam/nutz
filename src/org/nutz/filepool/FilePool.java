package org.nutz.filepool;

import java.io.File;
import java.io.IOException;

/**
 * 文件池
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface FilePool {

	/**
	 * @param fId
	 *            文件的 ID
	 * @param suffix
	 *            文件后缀名
	 * @return 是否存在这个文件
	 */
	boolean hasFile(long fId, String suffix);

	/**
	 * @return 当前池中最大的文件 ID 号
	 */
	long current();

	/**
	 * 从池中删除一个文件
	 * 
	 * @param fId
	 *            文件ID
	 * @param suffix
	 *            文件后缀名
	 * @return 被删除的文件
	 */
	File removeFile(long fId, String suffix);

	/**
	 * 在池中创建一个文件
	 * 
	 * @param suffix
	 *            文件的后缀
	 * @return 文件
	 * @throws IOException
	 */
	File createFile(String suffix);

	/**
	 * 获取一个文件在池中的 ID。 如果这个文件不在池中，返回 -1
	 * 
	 * @param f
	 *            文件
	 * @return 文件在池中的 ID，如果不在池中，返回 -1
	 */
	long getFileId(File f);

	/**
	 * 获取一个文件，如果文件不存在，返回null
	 * 
	 * @param fId
	 *            文件ID
	 * @param suffix
	 *            文件后缀名
	 * @return 文件对象
	 */
	File getFile(long fId, String suffix);

	/**
	 * 获取一个文件，如果文件不存在，创建它
	 * 
	 * @param fId
	 * @param suffix
	 * @return 文件对象
	 */
	File returnFile(long fId, String suffix);

	/**
	 * 清空文件池
	 * 
	 * @throws IOException
	 */
	void clear();

}
