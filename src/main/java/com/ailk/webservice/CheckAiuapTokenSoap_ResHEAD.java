package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by scj on 2016/8/24.
 */
@XStreamAlias("HEAD")
public class CheckAiuapTokenSoap_ResHEAD {

    public CheckAiuapTokenSoap_ResHEAD(){}

    /**
     * 消息标志，暂不填，预留	String	16
     */
    @XStreamAlias("CODE")
    public String CODE;

    /**
     * 消息序列号，暂不填，预留	String	16
     */
    @XStreamAlias("SID")
    public String SID;

    /**
     * 系统时间戳，格式为YYYYMMDDHHmmss	String	18
     */
    @XStreamAlias("TIMESTAMP")
    public String TIMESTAMP;

    /**
     * 应用标识，由4A统一分配，命名规则：移动公司省份两位简写+NG+系统缩写。如上海BOSS定义为SHNGBOSS，上海BOMC定义为SHNGBOMC，广西BOSS定义为GXNGBOSS，重庆BOSS定义CQNGBOSS等，4A的标识统一为AIUAP	String	16
     */
    @XStreamAlias("SERVICEID")
    public String SERVICEID;

    public CheckAiuapTokenSoap_ResHEAD(String CODE, String SID, String TIMESTAMP, String SERVICEID){
        this.CODE = CODE;
        this.SID = SID;
        this.TIMESTAMP = TIMESTAMP;
        this.SERVICEID = SERVICEID;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public String getTIMESTAMP() {
        return TIMESTAMP;
    }

    public void setTIMESTAMP(String TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }

    public String getSERVICEID() {
        return SERVICEID;
    }

    public void setSERVICEID(String SERVICEID) {
        this.SERVICEID = SERVICEID;
    }

}
