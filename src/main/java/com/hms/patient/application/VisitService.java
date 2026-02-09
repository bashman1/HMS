package com.hms.patient.application;

import com.hms.patient.application.dto.request.CreateVisitRequest;
import com.hms.patient.application.dto.response.VisitResponse;
import com.hms.patient.domain.model.entity.PatientVisit.VisitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for patient visit (OPD) operations.
 */
public interface VisitService {

    /**
     * Creates a new patient visit.
     *
     * @param request the visit creation request
     * @param createdBy the user ID of the creator
     * @return the created visit response
     */
    VisitResponse createVisit(CreateVisitRequest request, Long createdBy);

    /**
     * Updates visit status.
     *
     * @param uuid the visit UUID
     * @param status the new status
     * @param updatedBy the user ID of the updater
     * @return the updated visit response
     */
    VisitResponse updateVisitStatus(UUID uuid, VisitStatus status, Long updatedBy);

    /**
     * Gets a visit by UUID.
     *
     * @param uuid the visit UUID
     * @return the visit response
     */
    Optional<VisitResponse> getVisitByUuid(UUID uuid);

    /**
     * Gets a visit by visit number.
     *
     * @param visitNumber the visit number
     * @return the visit response
     */
    Optional<VisitResponse> getVisitByNumber(String visitNumber);

    /**
     * Gets visits by patient UUID.
     *
     * @param patientUuid the patient UUID
     * @return list of visit responses
     */
    List<VisitResponse> getVisitsByPatientUuid(UUID patientUuid);

    /**
     * Gets visits by patient ID with pagination.
     *
     * @param patientId the patient ID
     * @param pageable pagination information
     * @return page of visit responses
     */
    Page<VisitResponse> getVisitsByPatientId(Long patientId, Pageable pageable);

    /**
     * Gets today's visits for a department.
     *
     * @param departmentId the department ID
     * @param pageable pagination information
     * @return page of visit responses
     */
    Page<VisitResponse> getTodayVisitsByDepartment(Long departmentId, Pageable pageable);

    /**
     * Gets the queue for a department.
     *
     * @param departmentId the department ID
     * @return list of visit responses in queue order
     */
    List<VisitResponse> getDepartmentQueue(Long departmentId);

    /**
     * Gets visits by doctor and status.
     *
     * @param doctorId the doctor ID
     * @param status the visit status
     * @return list of visit responses
     */
    List<VisitResponse> getVisitsByDoctorAndStatus(Long doctorId, VisitStatus status);

    /**
     * Gets the next token number for a department.
     *
     * @param departmentId the department ID
     * @return the next token number
     */
    Integer getNextTokenNumber(Long departmentId);

    /**
     * Checks in a patient for their visit.
     *
     * @param uuid the visit UUID
     * @return the updated visit response
     */
    VisitResponse checkInPatient(UUID uuid);

    /**
     * Gets all visits with pagination.
     *
     * @param pageable pagination information
     * @return page of visit responses
     */
    Page<VisitResponse> getAllVisits(Pageable pageable);

    /**
     * Gets visits by status.
     *
     * @param status the visit status
     * @return list of visit responses
     */
    List<VisitResponse> getVisitsByStatus(VisitStatus status);
}
