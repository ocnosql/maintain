package com.ailk.webservice;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XStreamHandle {

    private static final String xmlString = "<books><book price=\"108\"><name>Java编程思想</name><author>Bruce Eckel</author></book><book price=\"52\"><name>Effective Java</name><author>Joshua Bloch</author></book><book price=\"118\"><name>Java 7入门经典</name><author>Ivor Horton</author></book></books>";

    public static String toXml(Object obj,boolean addTitle) {
        XStream xstream = new XStream(new DomDriver("utf8"));
        xstream.processAnnotations(obj.getClass()); // 识别obj类中的注解
        /*
         // 以压缩的方式输出XML
         StringWriter sw = new StringWriter();
         xstream.marshal(obj, new CompactWriter(sw));
         return sw.toString();
         */
        // 以格式化的方式输出XML
        String title = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        String content =  xstream.toXML(obj);
        if(content==null) return  null;
        if(addTitle) content = title+"\r\n"+content;
        return content;
    }
    
    public static <T> T toBean(String xmlStr, Class<T> cls) {
        if(xmlStr!=null&&xmlStr.startsWith("<?xml")){
            xmlStr = xmlStr.substring(xmlStr.indexOf(">")+1);
        }
        XStream xstream = new XStream(new DomDriver());
        xstream.ignoreUnknownElements();
        xstream.processAnnotations(cls);
        @SuppressWarnings("unchecked")
        T t = (T) xstream.fromXML(xmlStr);
        return t;
    }
    
    public static void main(String[] args) {
        UpdateAppAcctSoap_ReqHEAD test = new UpdateAppAcctSoap_ReqHEAD( "CODE",  "SID",  "TIMESTAMP", "SERVICEID");

        UpdateAppAcctSoap_ReqUSERMODIFYREQ test1 = new UpdateAppAcctSoap_ReqUSERMODIFYREQ();
        UpdateAppAcctSoap_ReqHEAD head = new UpdateAppAcctSoap_ReqHEAD();
        head.setCODE("code");
        head.setSID("sid");
        test1.setHEAD(head);
        UpdateAppAcctSoap_ReqBODY body = new UpdateAppAcctSoap_ReqBODY();
        body.setMODIFYMODE("add");
        UpdateAppAcctSoap_ReqUSERINFO userinfo = new UpdateAppAcctSoap_ReqUSERINFO();
        userinfo.setEMAIL("email");
        body.setUSERINFO(userinfo);
        test1.setBODY(body);
        System.out.print(toXml(test1,true));

        UpdateAppAcctSoap_ReqUSERMODIFYREQ temp =  toBean(toXml(test1,false), UpdateAppAcctSoap_ReqUSERMODIFYREQ.class);
        System.out.print("MODIFYMODE ="+temp.getBODY().getMODIFYMODE());

    }
}