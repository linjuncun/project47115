package com.hyperfit.util.wechat;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpXmlUtils {
    /**
     * 解析申请退款之后微信退款回调返回的字符串中内容
     *
     * @throws IOException
     * @throws JDOMException
     */
    public static Map<String, String> parseRefundNotifyXml(String refundXml) throws JDOMException, IOException {
        ParseXMLUtils.jdomParseXml(refundXml);
        StringReader read = new StringReader(refundXml);
        // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        // 创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();
        // 通过输入源构造一个Document
        Document doc;
        doc = (Document) sb.build(source);
        Element root = doc.getRootElement();// 指向根节点
        List<Element> list = root.getChildren();
        Map<String, String> resultMap = new HashMap<>();
        if (list != null && list.size() > 0) {
            for (Element element : list) {
                resultMap.put(element.getName(), element.getText());
            }
            return resultMap;
        }
        return null;
    }
}
