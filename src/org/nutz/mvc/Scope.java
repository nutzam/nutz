package org.nutz.mvc;

/**
 * 对象生命周期范围
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public enum Scope {

    /**
     * 所有范围
     */
    ALL,
    /**
     * ServletContext 级别
     */
    APP,
    /**
     * Session 级别
     */
    SESSION,
    /**
     * Request 级别
     */
    REQUEST

}
