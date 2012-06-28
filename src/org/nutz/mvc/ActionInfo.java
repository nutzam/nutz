package org.nutz.mvc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ActionInfo {

    private String inputEncoding;

    private String outputEncoding;

    private String pathKey;

    private String[] paths;

    private Map<String, String> pathMap;

    private String chainName;

    private ObjectInfo<? extends HttpAdaptor> adaptorInfo;

    private ViewMaker[] viewMakers;

    private String okView;

    private String failView;

    private List<String> httpMethods;

    private ObjectInfo<? extends ActionFilter>[] filterInfos;

    private String injectName;

    private Class<?> moduleType;

    private Method method;

    public ActionInfo() {
        httpMethods = new ArrayList<String>(4);
    }

    public ActionInfo mergeWith(ActionInfo parent) {
        // 组合路径 - 与父路径做一个笛卡尔积
        if (null != paths && null != parent.paths && parent.paths.length > 0) {
            List<String> myPaths = new ArrayList<String>(paths.length * parent.paths.length);
            for (int i = 0; i < parent.paths.length; i++) {
                String pp = parent.paths[i];
                for (int x = 0; x < paths.length; x++) {
                    myPaths.add(pp + paths[x]);
                }
            }
            paths = myPaths.toArray(new String[myPaths.size()]);
        }

        if (null == pathMap) {
            pathMap = parent.pathMap;
        } else {
            for (Entry<String, String> en : parent.pathMap.entrySet()) {
                if (pathMap.containsKey(en.getKey())) {
                    continue;
                }
                pathMap.put(en.getKey(), en.getValue());
            }
        }

        // 填充默认值
        inputEncoding = null == inputEncoding ? parent.inputEncoding : inputEncoding;
        outputEncoding = null == outputEncoding ? parent.outputEncoding : outputEncoding;
        adaptorInfo = null == adaptorInfo ? parent.adaptorInfo : adaptorInfo;
        okView = null == okView ? parent.okView : okView;
        failView = null == failView ? parent.failView : failView;
        filterInfos = null == filterInfos ? parent.filterInfos : filterInfos;
        injectName = null == injectName ? parent.injectName : injectName;
        moduleType = null == moduleType ? parent.moduleType : moduleType;
        chainName = null == chainName ? parent.chainName : chainName;
        return this;
    }

    /**
     * @return 这个入口函数是不是只匹配特殊的 http 方法。
     */
    public boolean isForSpecialHttpMethod() {
        return httpMethods.size() > 0;
    }

    /**
     * 只能接受如下字符串
     * <ul>
     * <li>GET
     * <li>PUT
     * <li>POST
     * <li>DELETE
     * </ul>
     * 
     * @return 特殊的 HTTP 方法列表
     */
    public List<String> getHttpMethods() {
        return httpMethods;
    }

    public String getPathKey() {
        return pathKey;
    }

    public void setPathKey(String pathKey) {
        this.pathKey = pathKey;
    }

    public String getInputEncoding() {
        return inputEncoding;
    }

    public void setInputEncoding(String inputEncoding) {
        this.inputEncoding = inputEncoding;
    }

    public String getOutputEncoding() {
        return outputEncoding;
    }

    public void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    public Map<String, String> getPathMap() {
        return pathMap;
    }

    public void setPathMap(Map<String, String> pathMap) {
        this.pathMap = pathMap;
    }

    public ObjectInfo<? extends HttpAdaptor> getAdaptorInfo() {
        return adaptorInfo;
    }

    public void setAdaptorInfo(ObjectInfo<? extends HttpAdaptor> adaptorInfo) {
        this.adaptorInfo = adaptorInfo;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public ViewMaker[] getViewMakers() {
        return viewMakers;
    }

    public void setViewMakers(ViewMaker[] makers) {
        this.viewMakers = makers;
    }

    public String getOkView() {
        return okView;
    }

    public void setOkView(String okView) {
        this.okView = okView;
    }

    public String getFailView() {
        return failView;
    }

    public void setFailView(String failView) {
        this.failView = failView;
    }

    public ObjectInfo<? extends ActionFilter>[] getFilterInfos() {
        return filterInfos;
    }

    public void setFilterInfos(ObjectInfo<? extends ActionFilter>[] filterInfos) {
        this.filterInfos = filterInfos;
    }

    public String getInjectName() {
        return injectName;
    }

    public void setInjectName(String injectName) {
        this.injectName = injectName;
    }

    public Class<?> getModuleType() {
        return moduleType;
    }

    public void setModuleType(Class<?> moduleType) {
        this.moduleType = moduleType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

}
