package com.stv.msgservice.xmlprocess;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Map;

public class XmlUtils {
    private static XStream xstream=createXStream();

    private static XStream createXStream() {
        xstream = new XStream(new NoneEscapeAppDriver());
        xstream.autodetectAnnotations(true);
        return xstream;
    }
    /**
     * 在指定路径生成xml文件
     *
     * @param t
     * @param xmlPath
     * @param <T>
     * @throws IOException
     */
    public static <T> void toXML(T t, Path xmlPath) throws IOException {
        File file = new File(xmlPath.toUri());
        if (!file.exists()) {
            file.createNewFile();
        }
        xstream.processAnnotations(t.getClass());
        FileOutputStream fos = new FileOutputStream(xmlPath.toString());
        fos.write(xstream.toXML(t).getBytes());
        fos.close();
    }

    public static <T> T fromXML(Path xmlPath) throws MalformedURLException {
        File file = new File(xmlPath.toUri());
        return (T) xstream.fromXML(file);
    }

    /**
     * 生成xml字符串
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> String toXML(T t) {
        return xstream.toXML(t);
    }

    public static <T> T fromXML(String xml) {
        return (T) xstream.fromXML(xml);
    }

    /**
     * 别名
     *
     * @param aliasMap 需要起别名的class，如果没有别名，Xstream会默认以Class的全限定名作为节点名称
     */
    public static void alias(Map<String, Class> aliasMap) {
        aliasMap.forEach((k, v) -> xstream.alias(k, v));
    }

    public static XStream getXstream() {
        return xstream;
    }
}
