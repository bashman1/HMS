package com.hms.patient.application.dto.response;

import com.hms.patient.domain.model.entity.PatientVisit.VisitStatus;
import com.hms.patient.domain.model.entity.PatientVisit.VisitType;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for patient visit responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitResponse {

    private UUID uuid;
    private String visitNumber;

    // Patient Information
    private UUID patientUuid;
    private String patientUhid;
    private String patientName;
    private Long patientId;

    private VisitType visitType;
    private LocalDateTime visitDate;
    private Long departmentId;
    private String departmentName;
    private Long doctorId;
    private String doctorName;
    private VisitStatus status;
    private String chiefComplaint;
    private String notes;

    // Consultation Details
    private String diagnosis;
    private String treatmentPlan;
    private String prescription;

    // Referral Details
    private Long referredDepartmentId;
    private String referredDepartmentName;
    private String referralReason;
    private Integer tokenNumber;
    private Integer priority;
    private Instant checkInTime;
    private Instant consultationStartTime;
    private Instant consultationEndTime;
    private Boolean followUp;
    private Long previousVisitId;
    private Double consultationFee;
    private Boolean billed;

    // Audit Fields
    private Instant createdAt;
    private Instant updatedAt;
}
