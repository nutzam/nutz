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
     * @return 当前池中最大的文件 ID 号
     */
    long current();

    /**
     * 判断文件池中是否存在一个文件
     * 
     * @param fId
     *            文件的 ID
     * @param suffix
     *            文件后缀名
     * @return 是否存在这个文件
     */
    boolean hasFile(long fId, String suffix);

    /**
     * 从池中删除一个文件，如果文件不存在，返回null
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
     * 判断文件池中是否存在一个临时目录
     * 
     * @param fId
     *            临时目录的 ID
     * 
     * @return 是否存在这个临时目录
     */
    boolean hasDir(long fId);

    /**
     * 从池中删除一个临时目录，如果文件不存在，返回null
     * 
     * @param fId
     *            临时目录ID
     * 
     * @return 被删除的目录
     */
    File removeDir(long fId);

    /**
     * 在池中创建一个临时目录
     * 
     * @return 临时目录
     * @throws IOException
     */
    File createDir();

    /**
     * 获取一个临时目录，如果临时目录不存在，返回null
     * 
     * @param fId
     *            临时目录ID
     * @return 临时目录对象
     */
    File getDir(long fId);

    /**
     * 获取一个临时目录，如果临时目录不存在，创建它
     * 
     * @param fId
     * 
     * @return 临时目录对象
     */
    File returnDir(long fId);

    /**
     * 清空文件池
     * 
     * @throws IOException
     */
    void clear();

}
