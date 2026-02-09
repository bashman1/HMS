package com.hms.patient.application.dto.request;

import com.hms.patient.domain.model.entity.Patient.BloodGroup;
import com.hms.patient.domain.model.entity.Patient.Gender;
import com.hms.patient.domain.model.entity.Patient.MaritalStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO for patient registration requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegistrationRequest {

    // Personal Information
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;

    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private BloodGroup bloodGroup;

    private MaritalStatus maritalStatus;

    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    private String nationality;

    @Size(max = 50, message = "Religion must not exceed 50 characters")
    private String religion;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;

    // Contact Information
    @NotBlank(message = "Primary phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phonePrimary;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid secondary phone number format")
    private String phoneSecondary;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
    private String addressLine2;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    // Emergency Contact
    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    private String emergencyContactName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid emergency contact phone number format")
    private String emergencyContactPhone;

    @Size(max = 50, message = "Emergency contact relation must not exceed 50 characters")
    private String emergencyContactRelation;

    // Identification
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must be 12 digits")
    private String aadhaarNumber;

    @Size(max = 20, message = "ABHA ID must not exceed 20 characters")
    private String abhaId;

    @Size(max = 20, message = "Passport number must not exceed 20 characters")
    private String passportNumber;

    // Medical Information
    @Size(max = 2000, message = "Allergies must not exceed 2000 characters")
    private String allergies;

    @Size(max = 2000, message = "Chronic conditions must not exceed 2000 characters")
    private String chronicConditions;
}
