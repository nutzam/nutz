package org.nutz.ioc.aop.config.impl;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.aop.impl.DefaultMirrorFactory;
import org.nutz.ioc.impl.NutIoc;
import org.xml.sax.SAXException;

public class XmlAopConfigrationTest {

    @Test
    public void testGetMirror() throws ParserConfigurationException, SAXException, IOException {
        DefaultMirrorFactory mirrorFactory = new DefaultMirrorFactory(null);
        mirrorFactory.setAopConfigration(new XmlAopConfigration("org/nutz/ioc/aop/config/impl/xmlfile-aop.xml"));
        assertNotNull(mirrorFactory.getMirror(XmlAopConfigration.class, null));
        assertNotNull(mirrorFactory.getMirror(DefaultMirrorFactory.class, null));
        assertNotNull(mirrorFactory.getMirror(NutDao.class, null));
        assertNotNull(mirrorFactory.getMirror(NutIoc.class, null));
    }

}
