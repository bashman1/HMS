package com.hms.patient.application.impl;

import com.hms.patient.application.dto.response.VisitResponse;
import com.hms.patient.domain.model.entity.PatientVisit;
import org.springframework.stereotype.Component;

/**
 * Mapper for PatientVisit entity and DTOs.
 */
@Component
public class VisitMapper {

    /**
     * Converts a PatientVisit entity to a VisitResponse DTO.
     *
     * @param visit the patient visit entity
     * @return the visit response DTO
     */
    public VisitResponse toResponse(PatientVisit visit) {
        if (visit == null) {
            return null;
        }

        return VisitResponse.builder()
                .uuid(visit.getUuid())
                .visitNumber(visit.getVisitNumber())
                .patientUuid(visit.getPatient() != null ? visit.getPatient().getUuid() : null)
                .patientUhid(visit.getPatient() != null ? visit.getPatient().getUhid() : null)
                .patientName(visit.getPatient() != null ? visit.getPatient().getFullName() : null)
                .patientId(visit.getPatient() != null ? visit.getPatient().getId() : null)
                .visitType(visit.getVisitType())
                .visitDate(visit.getVisitDate())
                .departmentId(visit.getDepartmentId())
                .departmentName(visit.getDepartmentName())
                .doctorId(visit.getDoctorId())
                .doctorName(visit.getDoctorName())
                .status(visit.getStatus())
                .chiefComplaint(visit.getChiefComplaint())
                .notes(visit.getNotes())
                .diagnosis(visit.getDiagnosis())
                .treatmentPlan(visit.getTreatmentPlan())
                .prescription(visit.getPrescription())
                .referredDepartmentId(visit.getReferredDepartmentId())
                .referredDepartmentName(visit.getReferredDepartmentName())
                .referralReason(visit.getReferralReason())
                .tokenNumber(visit.getTokenNumber())
                .priority(visit.getPriority())
                .checkInTime(visit.getCheckInTime())
                .consultationStartTime(visit.getConsultationStartTime())
                .consultationEndTime(visit.getConsultationEndTime())
                .followUp(visit.isFollowUp())
                .previousVisitId(visit.getPreviousVisitId())
                .consultationFee(visit.getConsultationFee())
                .billed(visit.isBilled())
                .createdAt(visit.getCreatedAt())
                .updatedAt(visit.getUpdatedAt())
                .build();
    }
}
