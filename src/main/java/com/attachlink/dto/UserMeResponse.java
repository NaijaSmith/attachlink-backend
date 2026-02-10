package com.attachlink.dto;

public class UserMeResponse {
    private String email;
    private String role;
    private String fullName;
    private String registrationNumber;
    private String institutionName;

    public UserMeResponse(String email, String role, String fullName, String registrationNumber, String institutionName) {
        this.email = email;
        this.role = role;
        this.fullName = fullName;
        this.registrationNumber = registrationNumber;
        this.institutionName = institutionName;
    }

    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getInstitutionName() { return institutionName; }
}
