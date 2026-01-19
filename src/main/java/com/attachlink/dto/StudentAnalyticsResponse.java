package com.attachlink.dto;

public class StudentAnalyticsResponse {

    private Long studentId;
    private long totalLogs;
    private long approvedLogs;
    private long rejectedLogs;
    private double approvalRate;
    private double averageEmployerRating;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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
}
