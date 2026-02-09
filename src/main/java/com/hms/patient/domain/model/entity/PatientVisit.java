package com.hms.patient.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * PatientVisit entity representing an OPD (Outpatient Department) visit.
 */
@Entity
@Table(name = "patient_visits", schema = "patient", indexes = {
        @Index(name = "idx_patient_visits_patient_id", columnList = "patient_id"),
        @Index(name = "idx_patient_visits_visit_number", columnList = "visit_number", unique = true),
        @Index(name = "idx_patient_visits_visit_date", columnList = "visit_date"),
        @Index(name = "idx_patient_visits_status", columnList = "status"),
        @Index(name = "idx_patient_visits_doctor_id", columnList = "doctor_id"),
        @Index(name = "idx_patient_visits_uuid", columnList = "uuid")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = { })
public class PatientVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "uuid", columnDefinition = "uuid", nullable = false, unique = true)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "visit_number", nullable = false, unique = true, length = 20)
    private String visitNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_type", nullable = false, length = 20)
    @Builder.Default
    private VisitType visitType = VisitType.OPD;

    @Column(name = "visit_date", nullable = false)
    @Builder.Default
    private LocalDateTime visitDate = LocalDateTime.now();

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "department_name", length = 100)
    private String departmentName;

    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "doctor_name", length = 200)
    private String doctorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private VisitStatus status = VisitStatus.REGISTERED;

    @Column(name = "chief_complaint", columnDefinition = "TEXT")
    private String chiefComplaint;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "token_number")
    private Integer tokenNumber;

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 0;

    @Column(name = "check_in_time")
    private Instant checkInTime;

    @Column(name = "consultation_start_time")
    private Instant consultationStartTime;

    @Column(name = "consultation_end_time")
    private Instant consultationEndTime;

    @Column(name = "is_follow_up")
    @Builder.Default
    private boolean followUp = false;

    @Column(name = "previous_visit_id")
    private Long previousVisitId;

    // Billing
    @Column(name = "consultation_fee")
    private Double consultationFee;

    @Column(name = "is_billed")
    @Builder.Default
    private boolean billed = false;

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

    public enum VisitType {
        OPD,           // Outpatient Department
        IPD,           // Inpatient Department
        EMERGENCY,     // Emergency visit
        FOLLOW_UP,     // Follow-up visit
        CONSULTATION   // Specialist consultation
    }

    public enum VisitStatus {
        REGISTERED,        // Patient registered
        IN_QUEUE,          // Waiting in queue
        IN_CONSULTATION,   // Currently with doctor
        COMPLETED,         // Consultation completed
        CANCELLED,         // Visit cancelled
        NO_SHOW,           // Patient did not show up
        REFERRED           // Referred to another department
    }
}
