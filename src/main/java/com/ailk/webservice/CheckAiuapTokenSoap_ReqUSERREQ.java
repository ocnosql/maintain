package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by scj on 2016/8/24.
 */
@XStreamAlias("USERREQ")
public class CheckAiuapTokenSoap_ReqUSERREQ {

    public CheckAiuapTokenSoap_ReqUSERREQ(){}

    @XStreamAlias("HEAD")
    public CheckAiuapTokenSoap_ReqHEAD HEAD;

    @XStreamAlias("BODY")
    public CheckAiuapTokenSoap_ReqBODY BODY;

    public CheckAiuapTokenSoap_ReqHEAD getHEAD() {
        return HEAD;
    }

    public void setHEAD(CheckAiuapTokenSoap_ReqHEAD HEAD) {
        this.HEAD = HEAD;
    }

    public CheckAiuapTokenSoap_ReqBODY getBODY() {
        return BODY;
    }

    public void setBODY(CheckAiuapTokenSoap_ReqBODY BODY) {
        this.BODY = BODY;
    }
}
