package com.ailk.webservice;

import com.ailk.util.DateUtil;
import com.ailk.util.PropertiesUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

import javax.xml.namespace.QName;
import java.util.Date;

// 动态构造调用串，灵活性更大
public class WsClient4A {

    public static final  String DATEFORMAT_PATTERN = "yyyyMMddHHmmss";
    private final static String WS_RUNTIME_PROPERTIES = "ws-runtime.properties";

    private String namespace;
    private String methodName;
    private String wsdlLocation;
    private String soapResponseData;

    public WsClient4A(){}

    public WsClient4A(String namespace, String methodName,
                      String wsdlLocation) {
        this.namespace = namespace;
        this.methodName = methodName;
        this.wsdlLocation = wsdlLocation;
    }

    public String invoke(Object[] obj){
        String result = null;
        try{
            RPCServiceClient client = new RPCServiceClient();
            Options options = client.getOptions();
            //String url = "http://10.182.20.39:7002/uac/services/CheckAiuapTokenSoap?wsdl";
            String url = this.wsdlLocation;
            EndpointReference end = new EndpointReference(url);
            options.setTo(end);

            //Object[] obj = new Object[]{"tom"};
            Class<?>[] classes = new Class[] { String.class };

            //QName qname = new QName("http://10.182.20.39:7002/uac/services/CheckAiuapTokenSoap", "CheckAiuapTokenSoap");
            QName qname = new QName(this.namespace, this.methodName);
            result = (String) client.invokeBlocking(qname, obj,classes)[0];
        }catch(AxisFault e){
            e.printStackTrace();
        }
        this.soapResponseData = result;
        return  result;
    }

    public CheckAiuapTokenSoap_ResUSERRSP getCheckAiuapTokenSoapRes(CheckAiuapTokenSoap_ReqUSERREQ reqUSERREQ){
        /**
        WsClient4A wsClient4A = new WsClient4A(
                "http://10.182.20.39:7002/uac/services/CheckAiuapTokenSoap",
                "CheckAiuapTokenSoap",
                "http://10.182.20.39:7002/uac/services/CheckAiuapTokenSoap?wsdl"
        );
        **/
        WsClient4A wsClient4A = new WsClient4A(
                get("ws.namespace4A"),
                get("ws.methodName4A"),
                get("ws.wsdlLocation4A")
        );

        Object[] obj = new Object[]{ XStreamHandle.toXml(reqUSERREQ,false)};
        CheckAiuapTokenSoap_ResUSERRSP resUSERRSP = new CheckAiuapTokenSoap_ResUSERRSP();
        try {
            String result = wsClient4A.invoke(obj);
            resUSERRSP = XStreamHandle.toBean(result,CheckAiuapTokenSoap_ResUSERRSP.class);
        }catch (Exception e){
            e.printStackTrace();
            CheckAiuapTokenSoap_ResHEAD resHEAD = new CheckAiuapTokenSoap_ResHEAD();
            resHEAD.setCODE("");
            resHEAD.setSID("");
            resHEAD.setSERVICEID(reqUSERREQ.getHEAD().getSERVICEID());
            resHEAD.setTIMESTAMP(DateUtil.format(new Date(), DATEFORMAT_PATTERN));

            CheckAiuapTokenSoap_ResBODY resBODY = new CheckAiuapTokenSoap_ResBODY();
            resBODY.setRSP("-1");
            resBODY.setMAINACCTID("");
            resBODY.setAPPACCTID(reqUSERREQ.getBODY().getAPPACCTID());

            resUSERRSP.setHEAD(resHEAD);
            resUSERRSP.setBODY(resBODY);
        }
        return resUSERRSP;
    }

    public String get(String propertyName){
        return PropertiesUtil.getProperty(WS_RUNTIME_PROPERTIES, propertyName);
    }

    public int getInt(String propertyName){
        return Integer.parseInt(PropertiesUtil.getProperty(WS_RUNTIME_PROPERTIES, propertyName));
    }

    public static void main(String[] args) throws Exception {
        WsClient4A wsClient4A = new WsClient4A();
        CheckAiuapTokenSoap_ReqUSERREQ reqUSERREQ = new CheckAiuapTokenSoap_ReqUSERREQ();

        CheckAiuapTokenSoap_ReqHEAD reqHEAD = new CheckAiuapTokenSoap_ReqHEAD();
        reqHEAD.setCODE("");
        reqHEAD.setSID("");
        reqHEAD.setTIMESTAMP(DateUtil.format(new Date(), DATEFORMAT_PATTERN));
        reqHEAD.setSERVICEID(wsClient4A.get("ws.serviceid4A"));

        CheckAiuapTokenSoap_ReqBODY reqBODY = new CheckAiuapTokenSoap_ReqBODY();
        reqBODY.setTOKEN("32|-101|62|-100|29|-74|-57|95|65|28|105|55|16|111|78|-110|101|-7|57|55|-26|-14|-118|31|77|22|-49|50|-1|-5|86|-64|89");
        reqBODY.setAPPACCTID("2000185534");

        reqUSERREQ.setHEAD(reqHEAD);
        reqUSERREQ.setBODY(reqBODY);

        System.out.println("-----------------------------------");
        System.out.println(XStreamHandle.toXml(reqUSERREQ, false));
        System.out.println("-----------------------------------");
        System.out.println(XStreamHandle.toXml(wsClient4A.getCheckAiuapTokenSoapRes(reqUSERREQ), false));
    }

}