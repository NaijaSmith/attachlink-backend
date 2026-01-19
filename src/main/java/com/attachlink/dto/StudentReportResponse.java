package com.attachlink.dto;

import java.util.List;

public class StudentReportResponse {

    private Long studentId;
    private String studentName;
    private String registrationNumber;

    private long totalLogs;
    private long approvedLogs;
    private long rejectedLogs;
    private double approvalRate;

    private double averageEmployerRating;
    private List<String> supervisorComments;

    // Getters & Setters

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public long getTotalLogs() {
        return totalLogs;
    }

    public void setTotalLogs(long totalLogs) {
        this.totalLogs = totalLogs;
    }

    public long getApprovedLogs() {
        return approvedLogs;
    }

    public void setApprovedLogs(long approvedLogs) {
        this.approvedLogs = approvedLogs;
    }

    public long getRejectedLogs() {
        return rejectedLogs;
    }

    public void setRejectedLogs(long rejectedLogs) {
        this.rejectedLogs = rejectedLogs;
    }

    public double getApprovalRate() {
        return approvalRate;
    }

    public void setApprovalRate(double approvalRate) {
        this.approvalRate = approvalRate;
    }

    public double getAverageEmployerRating() {
        return averageEmployerRating;
    }

    public void setAverageEmployerRating(double averageEmployerRating) {
        this.averageEmployerRating = averageEmployerRating;
    }

    public List<String> getSupervisorComments() {
        return supervisorComments;
    }

    public void setSupervisorComments(List<String> supervisorComments) {
        this.supervisorComments = supervisorComments;
    }
}
