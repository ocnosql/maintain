package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by scj on 2016/8/23.
 */
@XStreamAlias("USERMODIFYREQ")
public class UpdateAppAcctSoap_ReqUSERMODIFYREQ {

    public UpdateAppAcctSoap_ReqUSERMODIFYREQ(){}

    @XStreamAlias("HEAD")
    public UpdateAppAcctSoap_ReqHEAD HEAD;

    @XStreamAlias("BODY")
    public UpdateAppAcctSoap_ReqBODY BODY;

    public UpdateAppAcctSoap_ReqUSERMODIFYREQ(UpdateAppAcctSoap_ReqHEAD HEAD, UpdateAppAcctSoap_ReqBODY BODY){
        this.HEAD = HEAD;
        this.BODY = BODY;
    }

    public UpdateAppAcctSoap_ReqHEAD getHEAD() {
        return HEAD;
    }

    public void setHEAD(UpdateAppAcctSoap_ReqHEAD HEAD) {
        this.HEAD = HEAD;
    }

    public UpdateAppAcctSoap_ReqBODY getBODY() {
        return BODY;
    }

    public void setBODY(UpdateAppAcctSoap_ReqBODY BODY) {
        this.BODY = BODY;
    }
}
