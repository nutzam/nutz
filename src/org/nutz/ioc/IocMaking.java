package org.nutz.ioc;

import java.util.List;

import org.nutz.ioc.aop.MirrorFactory;
import org.nutz.ioc.meta.IocValue;
import org.nutz.json.Json;
import org.nutz.lang.Lang;

public class IocMaking {

    private String objectName;

    private ObjectMaker objectMaker;

    private Ioc ioc;

    private IocContext context;

    private List<ValueProxyMaker> vpms;

    private MirrorFactory mirrors;

    public IocMaking(    Ioc ioc,
                        MirrorFactory mirrors,
                        IocContext context,
                        ObjectMaker maker,
                        List<ValueProxyMaker> vpms,
                        String objName) {
        this.objectName = objName;
        this.objectMaker = maker;
        this.ioc = ioc;
        this.context = context;
        this.vpms = vpms;
        this.mirrors = mirrors;
    }

    public Ioc getIoc() {
        return ioc;
    }

    public IocContext getContext() {
        return context;
    }

    public String getObjectName() {
        return objectName;
    }

    public ObjectMaker getObjectMaker() {
        return objectMaker;
    }

    public MirrorFactory getMirrors() {
        return mirrors;
    }

    public IocMaking clone(String objectName) {
        return new IocMaking(ioc, mirrors, context, objectMaker, vpms, objectName);
    }

    public ValueProxy makeValue(IocValue iv) {
        for (ValueProxyMaker vpm : vpms) {
            ValueProxy vp = vpm.make(this, iv);
            if (null != vp)
                return vp;
        }
        throw Lang.makeThrow(    "Unknown value {'%s':%s} for object [%s]",
                                iv.getType(),
                                Json.toJson(iv.getValue()),
                                objectName);
    }

}
