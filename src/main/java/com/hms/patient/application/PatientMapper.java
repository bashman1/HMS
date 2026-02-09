package com.hms.patient.application;

import com.hms.patient.application.dto.request.PatientRegistrationRequest;
import com.hms.patient.application.dto.request.PatientUpdateRequest;
import com.hms.patient.application.dto.response.PatientResponse;
import com.hms.patient.domain.model.entity.Patient;
import org.springframework.stereotype.Component;

/**
 * Mapper for patient entity and DTOs.
 */
@Component
public class PatientMapper {

    /**
     * Converts a patient registration request to a patient entity.
     *
     * @param request the registration request
     * @param uhid the generated UHID
     * @param createdBy the user ID of the creator
     * @return the patient entity
     */
    public Patient toEntity(PatientRegistrationRequest request, String uhid, Long createdBy) {
        if (request == null) {
            return null;
        }

        return Patient.builder()
                .uhid(uhid)
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .bloodGroup(request.getBloodGroup())
                .maritalStatus(request.getMaritalStatus())
                .nationality(request.getNationality())
                .religion(request.getReligion())
                .occupation(request.getOccupation())
                .phonePrimary(request.getPhonePrimary())
                .phoneSecondary(request.getPhoneSecondary())
                .email(request.getEmail())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .emergencyContactRelation(request.getEmergencyContactRelation())
                .aadhaarNumber(request.getAadhaarNumber())
                .abhaId(request.getAbhaId())
                .passportNumber(request.getPassportNumber())
                .allergies(request.getAllergies())
                .chronicConditions(request.getChronicConditions())
                .active(true)
                .createdBy(createdBy)
                .build();
    }

    /**
     * Updates a patient entity from an update request.
     *
     * @param patient the patient entity to update
     * @param request the update request
     */
    public void updateEntity(Patient patient, PatientUpdateRequest request) {
        if (request == null) {
            return;
        }

        if (request.getFirstName() != null) {
            patient.setFirstName(request.getFirstName());
        }
        if (request.getMiddleName() != null) {
            patient.setMiddleName(request.getMiddleName());
        }
        if (request.getLastName() != null) {
            patient.setLastName(request.getLastName());
        }
        if (request.getDateOfBirth() != null) {
            patient.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            patient.setGender(request.getGender());
        }
        if (request.getBloodGroup() != null) {
            patient.setBloodGroup(request.getBloodGroup());
        }
        if (request.getMaritalStatus() != null) {
            patient.setMaritalStatus(request.getMaritalStatus());
        }
        if (request.getNationality() != null) {
            patient.setNationality(request.getNationality());
        }
        if (request.getReligion() != null) {
            patient.setReligion(request.getReligion());
        }
        if (request.getOccupation() != null) {
            patient.setOccupation(request.getOccupation());
        }
        if (request.getPhonePrimary() != null) {
            patient.setPhonePrimary(request.getPhonePrimary());
        }
        if (request.getPhoneSecondary() != null) {
            patient.setPhoneSecondary(request.getPhoneSecondary());
        }
        if (request.getEmail() != null) {
            patient.setEmail(request.getEmail());
        }
        if (request.getAddressLine1() != null) {
            patient.setAddressLine1(request.getAddressLine1());
        }
        if (request.getAddressLine2() != null) {
            patient.setAddressLine2(request.getAddressLine2());
        }
        if (request.getCity() != null) {
            patient.setCity(request.getCity());
        }
        if (request.getState() != null) {
            patient.setState(request.getState());
        }
        if (request.getPostalCode() != null) {
            patient.setPostalCode(request.getPostalCode());
        }
        if (request.getCountry() != null) {
            patient.setCountry(request.getCountry());
        }
        if (request.getEmergencyContactName() != null) {
            patient.setEmergencyContactName(request.getEmergencyContactName());
        }
        if (request.getEmergencyContactPhone() != null) {
            patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        }
        if (request.getEmergencyContactRelation() != null) {
            patient.setEmergencyContactRelation(request.getEmergencyContactRelation());
        }
        if (request.getAadhaarNumber() != null) {
            patient.setAadhaarNumber(request.getAadhaarNumber());
        }
        if (request.getAbhaId() != null) {
            patient.setAbhaId(request.getAbhaId());
        }
        if (request.getPassportNumber() != null) {
            patient.setPassportNumber(request.getPassportNumber());
        }
        if (request.getAllergies() != null) {
            patient.setAllergies(request.getAllergies());
        }
        if (request.getChronicConditions() != null) {
            patient.setChronicConditions(request.getChronicConditions());
        }
        if (request.getActive() != null) {
            patient.setActive(request.getActive());
        }
    }

    /**
     * Converts a patient entity to a response DTO.
     *
     * @param patient the patient entity
     * @return the patient response DTO
     */
    public PatientResponse toResponse(Patient patient) {
        if (patient == null) {
            return null;
        }

        return PatientResponse.builder()
                .id(patient.getId())
                .uuid(patient.getUuid())
                .uhid(patient.getUhid())
                .mrNumber(patient.getMrNumber())
                .firstName(patient.getFirstName())
                .middleName(patient.getMiddleName())
                .lastName(patient.getLastName())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getDateOfBirth())
                .age(patient.getAge())
                .gender(patient.getGender())
                .bloodGroup(patient.getBloodGroup())
                .maritalStatus(patient.getMaritalStatus())
                .nationality(patient.getNationality())
                .religion(patient.getReligion())
                .occupation(patient.getOccupation())
                .phonePrimary(patient.getPhonePrimary())
                .phoneSecondary(patient.getPhoneSecondary())
                .email(patient.getEmail())
                .addressLine1(patient.getAddressLine1())
                .addressLine2(patient.getAddressLine2())
                .city(patient.getCity())
                .state(patient.getState())
                .postalCode(patient.getPostalCode())
                .country(patient.getCountry())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .emergencyContactRelation(patient.getEmergencyContactRelation())
                .aadhaarNumber(patient.getAadhaarNumber())
                .abhaId(patient.getAbhaId())
                .passportNumber(patient.getPassportNumber())
                .allergies(patient.getAllergies())
                .chronicConditions(patient.getChronicConditions())
                .photoUrl(patient.getPhotoUrl())
                .active(patient.isActive())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}
