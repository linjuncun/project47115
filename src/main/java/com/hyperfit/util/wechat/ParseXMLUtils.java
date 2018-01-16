package com.hyperfit.util.wechat;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class ParseXMLUtils {
    /**
     * JDOM解析XML
     * 解析的时候自动去掉CDMA
     *
     * @param xml
     */
    @SuppressWarnings("unchecked")
    public static void jdomParseXml(String xml) {
        try {
            StringReader read = new StringReader(xml);
            // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
            InputSource source = new InputSource(read);
            // 创建一个新的SAXBuilder
            SAXBuilder sb = new SAXBuilder();
            // 通过输入源构造一个Document
            Document doc;
            doc = (Document) sb.build(source);

            Element root = doc.getRootElement();// 指向根节点
            List<Element> list = root.getChildren();

            if (list != null && list.size() > 0) {
                for (Element element : list) {
                    /*try{
                        methodName =  element.getName();
                        Method m = v.getClass().getMethod("set" + methodName, new Class[] { String.class });
                        if(parseInt(methodName)){
                            m.invoke(v, new Object[] { Integer.parseInt(element.getText()) });
                        }else{
                            m.invoke(v, new Object[] { element.getText() });
                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }*/

                }
            }

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
