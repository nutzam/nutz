package org.nutz.castor.model;

import java.io.Serializable;
import java.util.Date;

/**
 * create by zhouwenqing 2017/7/26 .
 */
public class BaseEntity<ID> implements Serializable{

    private static final long serialVersionUID = -5282762416250405030L;


    private ID id;

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    private Date createDate;




    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}
