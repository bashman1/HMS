package com.hms.auth.domain.model.entity;

/**
 * Enum representing different types of users in the hospital system.
 */
public enum UserType {
    /**
     * Hospital staff members (doctors, nurses, etc.)
     */
    STAFF,

    /**
     * Patients who use the hospital services
     */
    PATIENT,

    /**
     * External users (vendors, partners, etc.)
     */
    EXTERNAL,

    /**
     * System administrators
     */
    SYSTEM
}
