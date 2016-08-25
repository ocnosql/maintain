package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by scj on 2016/8/23.
 */
@XStreamAlias("USERMODIFYRSP")
public class UpdateAppAcctSoap_RspFUSERMODIFYRSP {

    public UpdateAppAcctSoap_RspFUSERMODIFYRSP(){}

    @XStreamAlias("HEAD")
    public UpdateAppAcctSoap_RspHEAD HEAD;

    @XStreamAlias("BODY")
    public UpdateAppAcctSoap_RspFBODY BODY;

    public UpdateAppAcctSoap_RspFUSERMODIFYRSP(UpdateAppAcctSoap_RspHEAD HEAD, UpdateAppAcctSoap_RspFBODY BODY){
        this.HEAD = HEAD;
        this.BODY = BODY;
    }

    public UpdateAppAcctSoap_RspHEAD getHEAD() {
        return HEAD;
    }

    public void setHEAD(UpdateAppAcctSoap_RspHEAD HEAD) {
        this.HEAD = HEAD;
    }

    public UpdateAppAcctSoap_RspFBODY getBODY() {
        return BODY;
    }

    public void setBODY(UpdateAppAcctSoap_RspFBODY BODY) {
        this.BODY = BODY;
    }

}
