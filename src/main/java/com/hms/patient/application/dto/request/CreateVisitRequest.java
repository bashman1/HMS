package com.hms.patient.application.dto.request;

import com.hms.patient.domain.model.entity.PatientVisit.VisitType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for creating OPD visits.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVisitRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Visit type is required")
    private VisitType visitType;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @Size(max = 100, message = "Department name must not exceed 100 characters")
    private String departmentName;

    private Long doctorId;

    @Size(max = 200, message = "Doctor name must not exceed 200 characters")
    private String doctorName;

    @Size(max = 2000, message = "Chief complaint must not exceed 2000 characters")
    private String chiefComplaint;

    private Boolean isFollowUp;

    private Integer priority;
}
