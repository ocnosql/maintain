package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by scj on 2016/8/23.
 */
@XStreamAlias("USERMODIFYRSP")
public class UpdateAppAcctSoap_RspTUSERMODIFYRSP {

    public UpdateAppAcctSoap_RspTUSERMODIFYRSP(){}

    @XStreamAlias("HEAD")
    public UpdateAppAcctSoap_RspHEAD HEAD;

    @XStreamAlias("BODY")
    public UpdateAppAcctSoap_RspTBODY BODY;

    public UpdateAppAcctSoap_RspTUSERMODIFYRSP(UpdateAppAcctSoap_RspHEAD HEAD, UpdateAppAcctSoap_RspTBODY BODY){
        this.HEAD = HEAD;
        this.BODY = BODY;
    }

    public UpdateAppAcctSoap_RspHEAD getHEAD() {
        return HEAD;
    }

    public void setHEAD(UpdateAppAcctSoap_RspHEAD HEAD) {
        this.HEAD = HEAD;
    }

    public UpdateAppAcctSoap_RspTBODY getBODY() {
        return BODY;
    }

    public void setBODY(UpdateAppAcctSoap_RspTBODY BODY) {
        this.BODY = BODY;
    }
}
