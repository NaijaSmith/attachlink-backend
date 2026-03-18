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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A lightweight Data Transfer Object used for populating selection dropdowns,
 * lists, and search results in the mobile and web interface.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {

    private Long id;
    
    private String fullName;
    
    private String email;
    
    private String institutionName;
    
    private String registrationNumber;

    /**
     * Custom toString method to make debugging easier 
     * and to provide a clean label for simple UI adapters.
     */
    @Override
    public String toString() {
        return registrationNumber != null ? 
            fullName + " (" + registrationNumber + ")" : 
            fullName;
    }
}