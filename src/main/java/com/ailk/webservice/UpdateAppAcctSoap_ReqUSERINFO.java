package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by scj on 2016/8/23.
 */
@XStreamAlias("USERINFO")
public class UpdateAppAcctSoap_ReqUSERINFO {

    public UpdateAppAcctSoap_ReqUSERINFO(){}

    /**
     * 需要变更的从帐号标识，对应应用的用户标识，如果对方无此字段，可以与LOGINNO保持一致；
     如果应用的用户标识为系统生成的，则新建帐号4A不传值，由应用生成后返回给4A	String	16
     */
    @XStreamAlias("USERID")
    public String USERID;

    /**
     * 从帐号登录名，如果应用的登录名为系统生成的，则新建帐号4A不传值，由应用生成后返回给4A	String	16
     */
    @XStreamAlias("LOGINNO")
    public String LOGINNO;

    /**
     * 人员姓名	String	16
     */
    @XStreamAlias("USERNAME")
    public String USERNAME;

    /**
     * 帐号状态：1正常；0加锁	String	1
     */
    @XStreamAlias("STATUS")
    public String  STATUS;

    /**
     * 归属组织标识	String	30
     */
    @XStreamAlias("ORGID")
    public String ORGID;

    /**
     * 电子邮件	String	16
     */
    @XStreamAlias("EMAIL")
    public String EMAIL;

    /**
     * 手机	String	16
     */
    @XStreamAlias("MOBILE")
    public String MOBILE;

    /**
     * 帐号密码	String	256
     */
    @XStreamAlias("PASSWORD")
    public String PASSWORD;

    /**
     * 生效时间	DATE
     */
    @XStreamAlias("EFFECTDATE")
    public String EFFECTDATE;

    /**
     * 失效时间	DATE
     */
    @XStreamAlias("EXPIREDATE")
    public String EXPIREDATE;

    /**
     * 备注	String	256
     */
    @XStreamAlias("REMARK")
    public String REMARK;


    public UpdateAppAcctSoap_ReqUSERINFO(String USERID, String LOGINNO,
                                         String USERNAME, String STATUS,
                                         String ORGID, String EMAIL,
                                         String MOBILE, String PASSWORD,
                                         String EFFECTDATE, String EXPIREDATE, String REMARK){
        this.USERID = USERID;
        this.LOGINNO = LOGINNO;
        this.USERNAME = USERNAME;
        this.STATUS = STATUS;
        this.ORGID = ORGID;
        this.EMAIL = EMAIL;
        this.MOBILE = MOBILE;
        this.PASSWORD = PASSWORD;
        this.EFFECTDATE = EFFECTDATE;
        this.EXPIREDATE = EXPIREDATE;
        this.REMARK = REMARK;
    }


    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public String getLOGINNO() {
        return LOGINNO;
    }

    public void setLOGINNO(String LOGINNO) {
        this.LOGINNO = LOGINNO;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getORGID() {
        return ORGID;
    }

    public void setORGID(String ORGID) {
        this.ORGID = ORGID;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getMOBILE() {
        return MOBILE;
    }

    public void setMOBILE(String MOBILE) {
        this.MOBILE = MOBILE;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getEFFECTDATE() {
        return EFFECTDATE;
    }

    public void setEFFECTDATE(String EFFECTDATE) {
        this.EFFECTDATE = EFFECTDATE;
    }

    public String getEXPIREDATE() {
        return EXPIREDATE;
    }

    public void setEXPIREDATE(String EXPIREDATE) {
        this.EXPIREDATE = EXPIREDATE;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }
}



