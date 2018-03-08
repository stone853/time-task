package com.apex.bss.cjsc.base;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jinsh on 2017/6/6.
 */
public class DomHelper {
    public static void main(String[] args) {
        String xml = "<xml><code><![CDATA[code]]></code><msg><![CDATA[msg]]></msg><responseId>12345678</responseId></xml>";
        System.out.println(parseXmlToList2(xml).toString());
    }


    private static Map parseXmlToList2(String xml){
        Map retMap = new HashMap();
        try {
            SAXBuilder sb = new SAXBuilder();
            if(xml ==null || "".equals(xml)){
                return null;
            }
            Document doc = sb.build(new InputSource(new StringReader(xml)));
            Element root = doc.getRootElement();// 指向根节点
            List<Element> es = root.getChildren();
            if (es != null && es.size() != 0) {
                for (Element element : es) {
                    retMap.put(element.getName(), element.getText());
                }
            }
        } catch (Exception e) {
            System.out.println("解析错误" + xml);
            e.printStackTrace();
        }
        return retMap;
    }
}
