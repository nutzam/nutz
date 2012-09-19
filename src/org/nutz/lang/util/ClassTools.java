package org.nutz.lang.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.nutz.Nutz;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class ClassTools {
    
    private static final Log log = Logs.get();

    /**
     * 从输入流中读取Class的名字,输入流必须是Class文件格式
     */
    public static String getClassName(InputStream in) {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(in));
            Map<Integer, String> strs = new HashMap<Integer, String>();
            Map<Integer, Integer> classes = new HashMap<Integer, Integer>();
            dis.skipBytes(4);//Magic
            dis.skipBytes(2);//副版本号
            dis.skipBytes(2);//主版本号
            
            //读取常量池
            int constant_pool_count = dis.readUnsignedShort();
            for (int i = 0; i < (constant_pool_count - 1); i++) {
                byte flag = dis.readByte();
                switch (flag) {
                case 7://CONSTANT_Class:
                    int index = dis.readUnsignedShort();
                    classes.put(i+1, index);
                    break;
                case 9://CONSTANT_Fieldref:
                case 10://CONSTANT_Methodref:
                case 11://CONSTANT_InterfaceMethodref:
                    dis.skipBytes(2);
                    dis.skipBytes(2);
                    break;
                case 8://CONSTANT_String:
                    dis.skipBytes(2);
                    break;
                case 3://CONSTANT_Integer:
                case 4://CONSTANT_Float:
                    dis.skipBytes(4);
                    break;
                case 5://CONSTANT_Long:
                case 6://CONSTANT_Double:
                    dis.skipBytes(8);
                    i++;//必须跳过一个,这是class文件设计的一个缺陷,历史遗留问题
                    break;
                case 12://CONSTANT_NameAndType:
                    dis.skipBytes(2);
                    dis.skipBytes(2);
                    break;
                case 1://CONSTANT_Utf8:
                    int len = dis.readUnsignedShort();
                    byte[] data = new byte[len];
                    dis.readFully(data);
                    strs.put(i + 1, new String(data, "UTF-8"));//必然是UTF8的
                    break;
                case 15://CONSTANT_MethodHandle:
                    dis.skipBytes(1);
                    dis.skipBytes(2);
                    break;
                case 16://CONSTANT_MethodType:
                    dis.skipBytes(2);
                    break;
                case 18://CONSTANT_InvokeDynamic:
                    dis.skipBytes(2);
                    dis.skipBytes(2);
                    break;
                default:
                    throw new RuntimeException("Impossible!! flag="+flag);
                }
            }
            
            dis.skipBytes(2);//版本控制符
            int pos = dis.readUnsignedShort();
            String name = strs.get(classes.get(pos));
            if (name != null)
                name = name.replace('/', '.');
            dis.close();
            return name;
        } catch (Throwable e) {
            if (log.isInfoEnabled()) 
                log.info("Fail to read ClassName from class InputStream", e);
        }
        return null;
    }
    
    private static ClassLoader nutClassLoader;
    static {
        nutClassLoader = Nutz.class.getClassLoader();
        //当使用JavaSE是,如果Nutz通过bootClassLoader加载,那么就会为null
        if (nutClassLoader == null)
            try {
                nutClassLoader = ClassLoader.getSystemClassLoader();
            }catch (Throwable e) {}
    }
    
    /**
     * 获取nutz.jar的ClassLoader的方法
     */
    public static ClassLoader getClassLoader() {
        return nutClassLoader;
    }
    
    @Deprecated
    public static void setNutClassLoader(ClassLoader nutClassLoader) {
        ClassTools.nutClassLoader = nutClassLoader;
    }
}
