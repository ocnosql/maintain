package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by scj on 2016/8/23.
 */
@XStreamAlias("USERMODIFYRES")
public class UpdateAppAcctSoap_ResTUSERMODIFYRES {

    public UpdateAppAcctSoap_ResTUSERMODIFYRES(){}

    @XStreamAlias("HEAD")
    public UpdateAppAcctSoap_ResHEAD HEAD;

    @XStreamAlias("BODY")
    public UpdateAppAcctSoap_ResTBODY BODY;

    public UpdateAppAcctSoap_ResTUSERMODIFYRES(UpdateAppAcctSoap_ResHEAD HEAD,UpdateAppAcctSoap_ResTBODY BODY){
        this.HEAD = HEAD;
        this.BODY = BODY;
    }

    public UpdateAppAcctSoap_ResHEAD getHEAD() {
        return HEAD;
    }

    public void setHEAD(UpdateAppAcctSoap_ResHEAD HEAD) {
        this.HEAD = HEAD;
    }

    public UpdateAppAcctSoap_ResTBODY getBODY() {
        return BODY;
    }

    public void setBODY(UpdateAppAcctSoap_ResTBODY BODY) {
        this.BODY = BODY;
    }
}
