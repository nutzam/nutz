package org.nutz.lang.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.nutz.lang.Encoding;
import org.nutz.lang.Streams;

public class ClassMetaReader {

    
    //---------------------------------------------------------------------------------------------------
    
    /**
     * 获取一个类的所有方法/构造方法的形参名称Map
     * @param klass 需要解析的类
     * @return 所有方法/构造方法的形参名称Map
     * @throws IOException 如果有任何IO异常,不应该有,如果是本地文件,那100%遇到bug了
     */
    public static Map<String, List<String>> getParamNames(Class<?> klass) throws IOException {
        InputStream in = klass.getResourceAsStream("/" + klass.getName().replace('.', '/') + ".class");
        try {
            if (in == null)
                return new HashMap<String, List<String>>();
            return build(in).paramNames;
        }
        finally {
            Streams.safeClose(in);
        }
    }
    
    public static ClassMeta build(InputStream in) throws IOException {
        ClassMeta meta = new ClassMeta();
        DataInputStream dis = new DataInputStream(new BufferedInputStream(in));
        Map<String, List<String>> names = new HashMap<String, List<String>>();
        Map<String, Integer> lines = new HashMap<String, Integer>();
        Map<Integer, String> strs = new HashMap<Integer, String>();
        Map<Integer, Integer> classIndex = new HashMap<Integer, Integer>();
        dis.skipBytes(4);//Magic
        dis.skipBytes(2);//副版本号
        dis.skipBytes(2);//主版本号
        
        //读取常量池
        int constant_pool_count = dis.readUnsignedShort();
        for (int i = 0; i < (constant_pool_count - 1); i++) {
            byte flag = dis.readByte();
            switch (flag) {
            case 7://CONSTANT_Class:
                int z = dis.readUnsignedShort();
                classIndex.put(i+1, z);
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
                strs.put(i + 1, new String(data, Encoding.UTF8));//必然是UTF8的
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
        //类名
        int z = dis.readUnsignedShort();
        meta.type = strs.get(classIndex.get(z));
        if (meta.type != null) {
            meta.type = meta.type.replace('/', '.');
        }
        dis.skipBytes(2);//超类
        
        //跳过接口定义
        int interfaces_count = dis.readUnsignedShort();
        dis.skipBytes(2 * interfaces_count);//每个接口数据,是2个字节
        
        //跳过字段定义
        int fields_count = dis.readUnsignedShort();
        for (int i = 0; i < fields_count; i++) {
            dis.skipBytes(2);
            dis.skipBytes(2);
            dis.skipBytes(2);
            int attributes_count = dis.readUnsignedShort();
            for (int j = 0; j < attributes_count; j++) {
                dis.skipBytes(2);//跳过访问控制符
                int attribute_length = dis.readInt();
                dis.skipBytes(attribute_length);
            }
        }
        
        //开始读取方法
        int methods_count = dis.readUnsignedShort();
        for (int i = 0; i < methods_count; i++) {
            dis.skipBytes(2); //跳过访问控制符
            String methodName = strs.get(dis.readUnsignedShort());
            String descriptor = strs.get(dis.readUnsignedShort());
            short attributes_count = dis.readShort();
            String key = methodName + "," + descriptor;
            for (int j = 0; j < attributes_count; j++) {
                String attrName = strs.get(dis.readUnsignedShort());
                int attribute_length = dis.readInt();
                if ("Code".equals(attrName)) { //形参只在Code属性中
                    dis.skipBytes(2);
                    dis.skipBytes(2);
                    int code_len = dis.readInt();
                    dis.skipBytes(code_len); //跳过具体代码
                    int exception_table_length = dis.readUnsignedShort();
                    dis.skipBytes(8 * exception_table_length); //跳过异常表
                    
                    int code_attributes_count = dis.readUnsignedShort();
                    for (int k = 0; k < code_attributes_count; k++) {
                        int str_index = dis.readUnsignedShort();
                        String codeAttrName = strs.get(str_index);
                        int code_attribute_length = dis.readInt();
                        if ("LocalVariableTable".equals(codeAttrName)) {//形参在LocalVariableTable属性中
                            int local_variable_table_length = dis.readUnsignedShort();
                            //按本地变量表的Slot属性升序排列，保证形参名称的位置与所对应参数的位置一致
                            TreeMap<Integer, String> varSlotNameMap = new TreeMap<Integer, String>();
                            for (int l = 0; l < local_variable_table_length; l++) {
                                dis.skipBytes(2);
                                dis.skipBytes(2);
                                String varName = strs.get(dis.readUnsignedShort());
                                dis.skipBytes(2);
                                int varSlot = dis.readUnsignedShort();//这是变量的位置
                                if (!"this".equals(varName)) //非静态方法,第一个参数是this
                                    varSlotNameMap.put(varSlot, varName);
                            }
                            
                            List<String> varNames = new ArrayList<String>(varSlotNameMap.values());
                            if (!names.containsKey(key))
                                names.put(key, varNames);
                        } 
                        else if ("LineNumberTable".equals(codeAttrName)) {
                            int len = dis.readUnsignedShort();
                            if (len > 0) {
                                dis.skipBytes(2);
                                int line = dis.readUnsignedShort();
                                dis.skipBytes(code_attribute_length - 6);
                                lines.put(key, line);
                            }
                        } else
                            dis.skipBytes(code_attribute_length);
                    }
                } else if ("MethodParameters".equals(attrName)) {
                    // JDK 8的参数名存储, 需要编译时加了-parameters 选项
                    // http://www.java-allandsundry.com/2013/12/java-8-parameter-name-at-runtime.html
                    int paramCount = dis.readByte();
                    List<String> varNames = new ArrayList<String>(paramCount);
                    for (int l = 0; l < paramCount; l++) {
                        String varName = strs.get(dis.readUnsignedShort());
                        dis.skipBytes(2);
                        if (!"this".equals(varName)) //非静态方法,第一个参数是this
                            varNames.add(varName);
                    }
                    names.put(key, varNames);
                } else
                    dis.skipBytes(attribute_length);
            }
        }
        dis.close();
        meta.paramNames.putAll(names);
        meta.methodLines.putAll(lines);
        return meta;
    }
    
    /**
     * 传入Method或Constructor,获取getParamNames方法返回的Map所对应的key
     */
    public static String getKey(Object obj) {
        StringBuilder sb = new StringBuilder();
        if (obj instanceof Method) {
            sb.append(((Method)obj).getName()).append(',');
            getDescriptor(sb, (Method)obj);
        } else if (obj instanceof Constructor) {
            sb.append("<init>,"); //只有非静态构造方法才能用有方法参数的,而且通过反射API拿不到静态构造方法
            getDescriptor(sb, (Constructor<?>)obj);
        } else
            throw new RuntimeException("Not Method or Constructor!");
        return sb.toString();
    }
    
    public static void getDescriptor(StringBuilder sb ,Method method){
        sb.append('(');
        for (Class<?> klass : method.getParameterTypes())
            getDescriptor(sb, klass);
        sb.append(')');
        getDescriptor(sb, method.getReturnType());
    }
    
    public static void getDescriptor(StringBuilder sb , Constructor<?> constructor){
        sb.append('(');
        for (Class<?> klass : constructor.getParameterTypes())
            getDescriptor(sb, klass);
        sb.append(')');
        sb.append('V');
    }
    
    /**本方法来源于ow2的asm库的Type类*/
    public static void getDescriptor(final StringBuilder buf, final Class<?> c) {
        Class<?> d = c;
        while (true) {
            if (d.isPrimitive()) {
                char car;
                if (d == Integer.TYPE) {
                    car = 'I';
                } else if (d == Void.TYPE) {
                    car = 'V';
                } else if (d == Boolean.TYPE) {
                    car = 'Z';
                } else if (d == Byte.TYPE) {
                    car = 'B';
                } else if (d == Character.TYPE) {
                    car = 'C';
                } else if (d == Short.TYPE) {
                    car = 'S';
                } else if (d == Double.TYPE) {
                    car = 'D';
                } else if (d == Float.TYPE) {
                    car = 'F';
                } else /* if (d == Long.TYPE) */{
                    car = 'J';
                }
                buf.append(car);
                return;
            } else if (d.isArray()) {
                buf.append('[');
                d = d.getComponentType();
            } else {
                buf.append('L');
                String name = d.getName();
                int len = name.length();
                for (int i = 0; i < len; ++i) {
                    char car = name.charAt(i);
                    buf.append(car == '.' ? '/' : car);
                }
                buf.append(';');
                return;
            }
        }
    }
}
