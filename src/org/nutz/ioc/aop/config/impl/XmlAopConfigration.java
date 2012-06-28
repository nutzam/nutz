package org.nutz.ioc.aop.config.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.nutz.lang.Lang;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * 通过Xml配置文件判断需要拦截哪些方法
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class XmlAopConfigration extends AbstractAopConfigration {

    public XmlAopConfigration(String... fileNames) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilder builder = Lang.xmls();
        Document document;
        List<AopConfigrationItem> aopList = new ArrayList<AopConfigrationItem>();
        List<NutResource> list = Scans.me().loadResource(null, fileNames);
        for (NutResource nutResource : list) {
            document = builder.parse(nutResource.getInputStream());
            document.normalizeDocument();
            NodeList nodeListZ = ((Element) document.getDocumentElement()).getElementsByTagName("class");
            for (int i = 0; i < nodeListZ.getLength(); i++)
                aopList.add(parse((Element) nodeListZ.item(i)));
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
