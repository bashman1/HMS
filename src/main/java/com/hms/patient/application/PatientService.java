package com.hms.patient.application;

import com.hms.patient.application.dto.request.PatientRegistrationRequest;
import com.hms.patient.application.dto.request.PatientUpdateRequest;
import com.hms.patient.application.dto.response.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for patient operations.
 */
public interface PatientService {

    /**
     * Registers a new patient.
     *
     * @param request the patient registration request
     * @param createdBy the user ID of the creator
     * @return the created patient response
     */
    PatientResponse registerPatient(PatientRegistrationRequest request, Long createdBy);

    /**
     * Updates an existing patient.
     *
     * @param uuid the patient UUID
     * @param request the update request
     * @param updatedBy the user ID of the updater
     * @return the updated patient response
     */
    PatientResponse updatePatient(UUID uuid, PatientUpdateRequest request, Long updatedBy);

    /**
     * Gets a patient by UUID.
     *
     * @param uuid the patient UUID
     * @return the patient response
     */
    Optional<PatientResponse> getPatientByUuid(UUID uuid);

    /**
     * Gets a patient by UHID.
     *
     * @param uhid the patient UHID
     * @return the patient response
     */
    Optional<PatientResponse> getPatientByUhid(String uhid);

    /**
     * Searches patients by query.
     *
     * @param query the search query
     * @param pageable pagination information
     * @return page of patient responses
     */
    Page<PatientResponse> searchPatients(String query, Pageable pageable);

    /**
     * Gets all active patients with pagination.
     *
     * @param pageable pagination information
     * @return page of patient responses
     */
    Page<PatientResponse> getAllActivePatients(Pageable pageable);

    /**
     * Deactivates a patient.
     *
     * @param uuid the patient UUID
     * @param updatedBy the user ID of the deactivator
     */
    void deactivatePatient(UUID uuid, Long updatedBy);

    /**
     * Activates a patient.
     *
     * @param uuid the patient UUID
     * @param updatedBy the user ID of the activator
     */
    void activatePatient(UUID uuid, Long updatedBy);
}
