package org.nutz.ioc.aop.impl;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.aop.config.imlp.XmlAopConfigration;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.lang.Files;
import org.xml.sax.SAXException;

public class XmlFileMirrorFactoryTest {

	@Test
	public void testGetMirror() throws ParserConfigurationException, SAXException, IOException {
		DefaultMirrorFactory mirrorFactory = new DefaultMirrorFactory(null);
		mirrorFactory.setAopConfigration(new XmlAopConfigration(Files.findFile("org/nutz/ioc/aop/impl/xmlfile-aop.xml").getPath()));
		assertNotNull(mirrorFactory.getMirror(XmlAopConfigration.class, null));
		assertNotNull(mirrorFactory.getMirror(DefaultMirrorFactory.class, null));
		assertNotNull(mirrorFactory.getMirror(NutDao.class, null));
		assertNotNull(mirrorFactory.getMirror(NutIoc.class, null));
	}

}
