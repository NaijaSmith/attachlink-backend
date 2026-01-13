package com.attachlink.dto;

import com.attachlink.entity.LogStatus;

public class LogReviewRequest {

    private LogStatus status;
    private String supervisorComment;

    public LogStatus getStatus() {
        return status;
    }

    public void setStatus(LogStatus status) {
        this.status = status;
    }

    public String getSupervisorComment() {
        return supervisorComment;
    }

    public void setSupervisorComment(String supervisorComment) {
        this.supervisorComment = supervisorComment;
    }
}