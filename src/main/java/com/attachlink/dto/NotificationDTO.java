package com.attachlink.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
private Long id;
private String message;
private LocalDateTime createdAt;
private boolean readStatus;

public NotificationDTO() {}

public NotificationDTO(Long id, String message, LocalDateTime createdAt, boolean readStatus) {
    this.id = id;
    this.message = message;
    this.createdAt = createdAt;
    this.readStatus = readStatus;
}

// Getters and Setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public String getMessage() { return message; }
public void setMessage(String message) { this.message = message; }
public LocalDateTime getCreatedAt() { return createdAt; }
public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
public boolean isReadStatus() { return readStatus; }
public void setReadStatus(boolean readStatus) { this.readStatus = readStatus; }


}