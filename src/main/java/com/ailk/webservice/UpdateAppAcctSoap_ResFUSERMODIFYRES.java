package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by scj on 2016/8/23.
 */
@XStreamAlias("USERMODIFYRES")
public class UpdateAppAcctSoap_ResFUSERMODIFYRES {

    public UpdateAppAcctSoap_ResFUSERMODIFYRES(){}

    @XStreamAlias("HEAD")
    public UpdateAppAcctSoap_ResHEAD HEAD;

    @XStreamAlias("BODY")
    public UpdateAppAcctSoap_ResFBODY BODY;

    public UpdateAppAcctSoap_ResFUSERMODIFYRES(UpdateAppAcctSoap_ResHEAD HEAD,UpdateAppAcctSoap_ResFBODY BODY){
        this.HEAD = HEAD;
        this.BODY = BODY;
    }

    public UpdateAppAcctSoap_ResHEAD getHEAD() {
        return HEAD;
    }

    public void setHEAD(UpdateAppAcctSoap_ResHEAD HEAD) {
        this.HEAD = HEAD;
    }

    public UpdateAppAcctSoap_ResFBODY getBODY() {
        return BODY;
    }

    public void setBODY(UpdateAppAcctSoap_ResFBODY BODY) {
        this.BODY = BODY;
    }

}
