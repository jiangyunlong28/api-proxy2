package com.hnttg.cibcredit.api.model;

public class CmpayCommonResp {

    private String responseTm;
    private String rspCd;
    private String rspInf;

    public CmpayCommonResp() {
    }

    public CmpayCommonResp(String responseTm, String rspCd, String rspInf) {
        this.responseTm = responseTm;
        this.rspCd = rspCd;
        this.rspInf = rspInf;
    }

    public String getResponseTm() {
        return responseTm;
    }

    public void setResponseTm(String responseTm) {
        this.responseTm = responseTm;
    }

    public String getRspCd() {
        return rspCd;
    }

    public void setRspCd(String rspCd) {
        this.rspCd = rspCd;
    }

    public String getRspInf() {
        return rspInf;
    }

    public void setRspInf(String rspInf) {
        this.rspInf = rspInf;
    }
}
