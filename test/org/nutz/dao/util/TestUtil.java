package org.nutz.dao.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Haiming
 * @date 2020/8/14 9:22 AM
 */
public class TestUtil {
    /**
     * 读取文件
     *
     * @param path
     * @return
     */
    public static String getFileData(String path) {
        ClassLoader cl = TestUtil.class.getClassLoader();
        InputStream is = cl.getResourceAsStream(path);
        //读取文件
        StringBuffer sb = new StringBuffer();
        //这里可以控制编码
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String data = new String(sb);
        return data;
    }
}
