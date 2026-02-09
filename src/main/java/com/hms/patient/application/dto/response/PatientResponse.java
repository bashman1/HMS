package com.hms.patient.application.dto.response;

import com.hms.patient.domain.model.entity.Patient.BloodGroup;
import com.hms.patient.domain.model.entity.Patient.Gender;
import com.hms.patient.domain.model.entity.Patient.MaritalStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for patient responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {

    private Long id;
    private UUID uuid;
    private String uhid;
    private String mrNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private Integer age;
    private Gender gender;
    private BloodGroup bloodGroup;
    private MaritalStatus maritalStatus;
    private String nationality;
    private String religion;
    private String occupation;

    // Contact Information
    private String phonePrimary;
    private String phoneSecondary;
    private String email;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;

    // Identification
    private String aadhaarNumber;
    private String abhaId;
    private String passportNumber;

    // Medical Information
    private String allergies;
    private String chronicConditions;
    private String photoUrl;
    private Boolean active;

    // Audit Fields
    private Instant createdAt;
    private Instant updatedAt;
}
