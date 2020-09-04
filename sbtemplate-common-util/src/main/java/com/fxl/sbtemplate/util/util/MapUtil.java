package com.fxl.sbtemplate.util.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;

public class MapUtil {

    public static Map<String, String > getStringMap(Map<String, String[]> map) {
        Map<String, String> result = new HashMap<>(map.size());
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String[] values = map.get(key);
            result.put(key, String.valueOf(values.length == 0 ? "": values[0]));
        }
        return result;
    }

    public static Map<String, Object> object2Map(Object object){
        Map<String,Object> result=new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String name = new String(field.getName());
                result.put(name, field.get(object));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String toXml(Map<String, String> data) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key: data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString();
        try {
            writer.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        output = output.substring(output.indexOf("?>")+2,output.length());
        return output;
    }
    
    /**
     * 校验Map请求参数是否缺少必填项参数
     * @date 2020/8/11 15:05
     * @auther fangxilin 
     * @param params
     * @param paramKeys 需校验的key
     * @return boolean true验证通过，false验证不通过
     */
    public static boolean checkMapParams(Map<String, Object> params, String... paramKeys) {
        if (params == null || params.size() == 0) {
            return false;
        }
        if (paramKeys == null) { //无要校验的参数
            return true;
        }
        List<String> keys = Arrays.asList(paramKeys);
        for (String key : keys) {
            if (!params.containsKey(key) || params.get(key) == null) {
                return false;
            }
        }
        return true;
    }
}
