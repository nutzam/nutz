package org.nutz.mvc.view.redirect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

public class MyModule {

    @At("/register")
    @Ok("redirect:/jsp/user/information.nut?id=${obj.id}")
    public User register(
                    HttpServletRequest request,
                    HttpSession session) {
            User user = new User();
            user.setId(373);
            return user;
    }
}
