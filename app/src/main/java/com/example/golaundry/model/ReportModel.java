package com.example.golaundry.model;

public class ReportModel {
    private String reportId;
    private String reporterRole;
    private String reporterId;
    private String userId;
    private String orderId;
    private String rateId;
    private String currentDateTime;
    private String messageFromReporter;
    private boolean adminResponse;

    public ReportModel(String reportId, String reporterRole, String reporterId, String userId, String orderId, String rateId, String currentDateTime, String messageFromReporter, boolean adminResponse) {
        this.reportId = reportId;
        this.reporterRole = reporterRole;
        this.reporterId = reporterId;
        this.userId = userId;
        this.orderId = orderId;
        this.rateId = rateId;
        this.currentDateTime = currentDateTime;
        this.messageFromReporter = messageFromReporter;
        this.adminResponse = adminResponse;
    }

    public ReportModel() {
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReporterRole() {
        return reporterRole;
    }

    public void setReporterRole(String reporterRole) {
        this.reporterRole = reporterRole;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRateId() {
        return rateId;
    }

    public void setRateId(String rateId) {
        this.rateId = rateId;
    }

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    public String getMessageFromReporter() {
        return messageFromReporter;
    }

    public void setMessageFromReporter(String messageFromReporter) {
        this.messageFromReporter = messageFromReporter;
    }

    public boolean isAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(boolean adminResponse) {
        this.adminResponse = adminResponse;
    }
}
