package org.nutz.mvc.upload;

import java.io.Serializable;

/**
 * 当用户通过传统的 http 方式上传文件时。 UploadAdaptor 会负责上传流的解析 <br>
 * 此时，它会创建这个对象，并保存在会话对象中（名字参见 UploadInfo.SESSION_NAME 常量）<br>
 * <br>
 * 用户可以随时通过 HTTP 查看当前 session 中这个对象，来获知上传的进度。 <br>
 * 用户也可以将这个对象的 stop 属性设置成 true，<br>
 * 那么 UploadAdaptor 会终止上传(通过抛出 UploadStopException)<br>
 * 并将这个对象的 sum 和 current 属性均设成 -2
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class UploadInfo implements Serializable {

    private static final long serialVersionUID = 1145316598297147074L;

    public static final String SESSION_NAME = "UPLOAD_INFO";

    /**
     * 本次上传，流的总大小。单位 byte
     */
    public long sum;

    /**
     * 当前已经读取的字节数
     */
    public long current;

    /**
     * 是否要立刻停止
     */
    public boolean stop;

    public UploadInfo clone() {
        UploadInfo old = new UploadInfo();
        old.sum = sum;
        old.current = current;
        stop = true;
        return old;
    }

}
