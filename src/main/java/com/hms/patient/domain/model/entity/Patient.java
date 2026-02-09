package com.hms.patient.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Patient entity representing a patient in the hospital management system.
 */
@Entity
@Table(name = "patients", schema = "patient", indexes = {
        @Index(name = "idx_patients_uhid", columnList = "uhid", unique = true),
        @Index(name = "idx_patients_phone", columnList = "phone_primary"),
        @Index(name = "idx_patients_email", columnList = "email"),
        @Index(name = "idx_patients_name", columnList = "last_name"),
        @Index(name = "idx_patients_uuid", columnList = "uuid")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = { })
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "uuid", columnDefinition = "uuid", nullable = false, unique = true)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @Column(name = "uhid", nullable = false, unique = true, length = 20)
    private String uhid;

    @Column(name = "mr_number", length = 20)
    private String mrNumber;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", length = 5)
    private BloodGroup bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    private MaritalStatus maritalStatus;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "religion", length = 50)
    private String religion;

    @Column(name = "occupation", length = 100)
    private String occupation;

    // Contact Information
    @Column(name = "phone_primary", nullable = false, length = 20)
    private String phonePrimary;

    @Column(name = "phone_secondary", length = 20)
    private String phoneSecondary;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 100)
    private String country;

    // Emergency Contact
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "emergency_contact_relation", length = 50)
    private String emergencyContactRelation;

    // Identification
    @Column(name = "aadhaar_number", length = 12)
    private String aadhaarNumber;

    @Column(name = "abha_id", length = 20)
    private String abhaId;

    @Column(name = "passport_number", length = 20)
    private String passportNumber;

    // Medical Information
    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "chronic_conditions", columnDefinition = "TEXT")
    private String chronicConditions;

    // Photo
    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    // Status
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    // Audit Fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * Gets the full name of the patient.
     */
    public String getFullName() {
        return String.format("%s %s %s",
                firstName != null ? firstName : "",
                middleName != null ? middleName : "",
                lastName != null ? lastName : "").trim();
    }

    /**
     * Calculates the age based on date of birth.
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public enum Gender {
        MALE, FEMALE, OTHER, UNKNOWN
    }

    public enum BloodGroup {
        A_POSITIVE, A_NEGATIVE,
        B_POSITIVE, B_NEGATIVE,
        AB_POSITIVE, AB_NEGATIVE,
        O_POSITIVE, O_NEGATIVE,
        UNKNOWN
    }

    public enum MaritalStatus {
        SINGLE, MARRIED, DIVORCED, WIDOWED, SEPARATED, UNKNOWN
    }
}
