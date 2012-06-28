package org.nutz.json.generic;

import org.nutz.json.JsonField;
import org.nutz.lang.util.NutType;

public class JsonRequest<T> {

    /**
     * 会员
     */
    public static final int USER_TYPE_MEMBER = 0;

    /**
     * 员工
     */
    public static final int USER_TYPE_EMPLOYEE = 1;

    /**
     * 登陆门店id
     */
    private String branchId;

    /**
     * 登陆用户ID
     */
    private String userId;

    /**
     * 登陆名
     */
    private String loginName;

    /**
     * 登陆密码
     */
    private String password;

    /**
     * 用户类型，0为会员，1为员工
     */
    private Integer userType;

    /**
     * 接口版本号
     */
    private String version;

    @JsonField(ignore = true)
    private NutType nutType;

    /**
     * 请求参数主体
     */
    // @JsonField(createBy = "createBody")
    private T body;

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public NutType getNutType() {
        return nutType;
    }

    public void setNutType(NutType nutType) {
        this.nutType = nutType;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    // public Object createBody(Type type, Object value) {
    // Class<?> clazz = null;
    //
    // if (nutType != null) {
    // // 有泛型信息
    // ParameterizedType pt = (ParameterizedType) nutType;
    // Type[] ts = pt.getActualTypeArguments();
    // if (ts != null) {
    // clazz = Lang.getTypeClass(ts[0]);
    // }
    // }
    //
    // if (clazz == null) {
    // clazz = Record.class;
    // }
    //
    // return JsonParsing.convert(clazz, value);
    // }

}
