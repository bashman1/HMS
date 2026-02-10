package com.hms.patient.application.dto.request;

import com.hms.patient.domain.model.entity.PatientVisit.VisitType;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for updating patient visit details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVisitRequest {

    private VisitType visitType;

    private Long departmentId;

    @Size(max = 100, message = "Department name must not exceed 100 characters")
    private String departmentName;

    private Long doctorId;

    @Size(max = 200, message = "Doctor name must not exceed 200 characters")
    private String doctorName;

    @Size(max = 2000, message = "Chief complaint must not exceed 2000 characters")
    private String chiefComplaint;

    @Size(max = 4000, message = "Notes must not exceed 4000 characters")
    private String notes;

    private Boolean isFollowUp;

    private Integer priority;

    // Consultation details
    @Size(max = 2000, message = "Diagnosis must not exceed 2000 characters")
    private String diagnosis;

    @Size(max = 4000, message = "Treatment plan must not exceed 4000 characters")
    private String treatmentPlan;

    @Size(max = 4000, message = "Prescription must not exceed 4000 characters")
    private String prescription;

    // Referral details
    private Long referredDepartmentId;

    @Size(max = 100, message = "Referred department name must not exceed 100 characters")
    private String referredDepartmentName;

    @Size(max = 500, message = "Referral reason must not exceed 500 characters")
    private String referralReason;
}
