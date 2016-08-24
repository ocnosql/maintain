package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by scj on 2016/8/24.
 */
@XStreamAlias("USERRSP")
public class CheckAiuapTokenSoap_ResUSERRSP {

    public CheckAiuapTokenSoap_ResUSERRSP(){}

    @XStreamAlias("HEAD")
    public CheckAiuapTokenSoap_ResHEAD HEAD;

    @XStreamAlias("BODY")
    public CheckAiuapTokenSoap_ResBODY BODY;

    public CheckAiuapTokenSoap_ResHEAD getHEAD() {
        return HEAD;
    }

    public void setHEAD(CheckAiuapTokenSoap_ResHEAD HEAD) {
        this.HEAD = HEAD;
    }

    public CheckAiuapTokenSoap_ResBODY getBODY() {
        return BODY;
    }

    public void setBODY(CheckAiuapTokenSoap_ResBODY BODY) {
        this.BODY = BODY;
    }
}
