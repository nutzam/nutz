package org.nutz.dao.test.meta;

import org.nutz.lang.random.R;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : haiming
 * @date : 2020-02-27
 */
public abstract class BaseBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public String uuid() {
        return R.UU32().toLowerCase();
    }

    public Date now() {
        return new Date();
    }
}
