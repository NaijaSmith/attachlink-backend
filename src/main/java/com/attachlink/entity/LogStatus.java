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
package com.attachlink.entity;

import lombok.Getter;

/**
 * Represents the workflow state of a LogEntry within the system.
 */
@Getter
public enum LogStatus {
    
    /**
     * The entry has been saved by the student and sent to the supervisor.
     */
    SUBMITTED("Submitted"),

    /**
     * The entry is being created or edited by the student.
     */
    DRAFT("Draft"),

    /**
     * The supervisor has viewed the entry but has not finalized a decision.
     */
    REVIEWED("Reviewed"),

    /**
     * The entry meets requirements and has been formally accepted.
     */
    APPROVED("Approved"),

    /**
     * The entry was sent back for corrections or dismissed.
     */
    REJECTED("Rejected"),

    /**
     * The entry was edited and resubmitted.
     */
    RESUBMITTED("Resubmitted");

    private final String displayName;

    LogStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Utility to check if the status represents a finished workflow.
     * @return true if status is APPROVED or REJECTED.
     */
    public boolean isFinalized() {
        return this == APPROVED || this == REJECTED;
    }
}