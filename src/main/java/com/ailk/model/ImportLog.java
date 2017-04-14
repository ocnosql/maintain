package com.ailk.model;

/**
 * Created by wsh on 2016/8/11.
 */
public class ImportLog {
    private String id;
    private String tablex;
    private String schemax;
    private String status;
    private String importdate;
    private String input;
    private String output;
    private String total;
    private String success_total;
    private String fail_total;
    private String finalStatus;
    private String mrStatus;
    private String completeBulkloadStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTablex() {
        return tablex;
    }

    public void setTablex(String tablex) {
        this.tablex = tablex;
    }

    public String getSchemax() {
        return schemax;
    }

    public void setSchemax(String schemax) {
        this.schemax = schemax;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImportdate() {
        return importdate;
    }

    public void setImportdate(String importdate) {
        this.importdate = importdate;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSuccess_total() {
        return success_total;
    }

    public void setSuccess_total(String success_total) {
        this.success_total = success_total;
    }

    public String getFail_total() {
        return fail_total;
    }

    public void setFail_total(String fail_total) {
        this.fail_total = fail_total;
    }

    public String getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(String finalStatus) {
        this.finalStatus = finalStatus;
    }

    public String getMrStatus() {
        return mrStatus;
    }

    public void setMrStatus(String mrStatus) {
        this.mrStatus = mrStatus;
    }

    public String getCompleteBulkloadStatus() {
        return completeBulkloadStatus;
    }

    public void setCompleteBulkloadStatus(String completeBulkloadStatus) {
        this.completeBulkloadStatus = completeBulkloadStatus;
    }
}
