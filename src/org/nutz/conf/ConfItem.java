package org.nutz.conf;

public class ConfItem {
    private String type;
    private String[] args;
    private Class<?> clazz;
    public String getType() {
        return type;
    }
    /**
     * 取得类型
     * @return
     */
    public Class<?> getClazz(){
        if(clazz != null){
            return clazz;
        }
        if(type == null || type.equals("")){
            return null;
        }
        try {
            clazz = this.getClass().getClassLoader().loadClass(type);
        } catch (ClassNotFoundException e) {
        }
        return clazz;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String[] getArgs() {
        return args;
    }
    public void setArgs(String[] args) {
        this.args = args;
    }
}
