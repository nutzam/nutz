package org.nutz.mvc;


public interface Loading {

    String CONTEXT_NAME = "_NUTZ_LOADING_CONTEXT_";

    UrlMapping load(NutConfig config);

    void depose(NutConfig config);

}
