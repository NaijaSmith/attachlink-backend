/*Copyright 2026 Nicholas Kariuki Wambui

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
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
