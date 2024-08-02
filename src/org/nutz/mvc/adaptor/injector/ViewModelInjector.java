package org.nutz.mvc.adaptor.injector;

import org.nutz.mvc.ViewModel;
import org.nutz.mvc.adaptor.ParamInjector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ViewModelInjector implements ParamInjector {

    @Override
    public Object get(ServletContext sc,
                      HttpServletRequest req,
                      HttpServletResponse resp,
                      Object refer) {
        return new ViewModel();
    }

}
