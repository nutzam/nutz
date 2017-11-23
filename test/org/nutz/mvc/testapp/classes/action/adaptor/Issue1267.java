package org.nutz.mvc.testapp.classes.action.adaptor;

import java.util.Date;

import org.nutz.mvc.annotation.Param;

public class Issue1267 {

    @Param(value="time", dfmt="EEE MMM dd yyyy hh:mm:ss 'GMT'Z (z)", locale="en")
    private Date time;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
