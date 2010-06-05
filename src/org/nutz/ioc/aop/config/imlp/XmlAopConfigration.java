package org.nutz.ioc.aop.config.imlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlAopConfigration extends AbstractAopConfigration {
	
	public XmlAopConfigration(String... fileNames) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = Lang.xmls();
        Document document;
        List<AopConfigrationItem> aopList = new ArrayList<AopConfigrationItem>();
        for (String fileName : fileNames) {
                document = builder.parse(Files.findFile(fileName));
                document.normalizeDocument();
                NodeList nodeListZ = ((Element) document.getDocumentElement()).getElementsByTagName("class");
                for (int i = 0; i < nodeListZ.getLength(); i++)
                	aopList.add(parse((Element)nodeListZ.item(i)));
        }
        setAopItemList(aopList);
	}

	private AopConfigrationItem parse(Element item) {
		AopConfigrationItem aopItem = new AopConfigrationItem();
		aopItem.setClassName(item.getAttribute("name"));
		aopItem.setMethodName(item.getAttribute("method"));
		aopItem.setInterceptor(item.getAttribute("interceptor"));
		if (item.hasAttribute("singleton"))
			aopItem.setSingleton(Boolean.parseBoolean(item.getAttribute("singleton")));
		return aopItem;
	}

}
