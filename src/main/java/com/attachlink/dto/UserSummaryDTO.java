/*
 * Copyright 2026 Nicholas Kariuki Wambui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.attachlink.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A lightweight Data Transfer Object used for selection dropdowns and lists.
 * Refined with Jackson annotations to bridge snake_case (Android) and camelCase (Spring).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {

    private Long id;
    
    @JsonProperty("full_name")
    @JsonAlias("fullName")
    private String fullName;
    
    private String email;
    
    @JsonProperty("institution_name")
    @JsonAlias({"institutionName", "institution"})
    private String institutionName;
    
    @JsonProperty("registration_number")
    @JsonAlias("registrationNumber")
    private String registrationNumber;

    /**
     * Refined toString for server-side logging and simple adapters.
     * Matches the logic in the Android UserSummary for consistency.
     */
    @Override
    public String toString() {
        String name = (fullName != null) ? fullName : "User " + id;
        
        if (registrationNumber != null && !registrationNumber.isEmpty()) {
            return name + " (" + registrationNumber + ")";
        }
        
        if (institutionName != null && !institutionName.isEmpty()) {
            return name + " [" + institutionName + "]";
        }
        
        return name;
    }
}