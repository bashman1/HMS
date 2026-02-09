package com.hms.patient.application.impl;

import com.hms.patient.application.PatientMapper;
import com.hms.patient.application.VisitService;
import com.hms.patient.application.dto.request.CreateVisitRequest;
import com.hms.patient.application.dto.response.VisitResponse;
import com.hms.patient.domain.model.entity.Patient;
import com.hms.patient.domain.model.entity.PatientVisit;
import com.hms.patient.domain.model.entity.PatientVisit.VisitStatus;
import com.hms.patient.domain.port.PatientRepository;
import com.hms.patient.domain.port.PatientVisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of VisitService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VisitServiceImpl implements VisitService {

    private final PatientVisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final VisitMapper visitMapper;

    private static final String VISIT_NUMBER_PREFIX = "VN";
    private static final DateTimeFormatter VISIT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public VisitResponse createVisit(CreateVisitRequest request, Long createdBy) {
        log.info("Creating new visit for patient: {}", request.getPatientId());

        // Validate patient exists
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        // Generate unique visit number
        String visitNumber = generateVisitNumber();
        while (visitRepository.existsByVisitNumber(visitNumber)) {
            visitNumber = generateVisitNumber();
        }

        // Get next token number
        Integer tokenNumber = getNextTokenNumber(request.getDepartmentId());

        // Create visit
        PatientVisit visit = PatientVisit.builder()
                .patient(patient)
                .visitNumber(visitNumber)
                .visitType(request.getVisitType())
                .visitDate(LocalDateTime.now())
                .departmentId(request.getDepartmentId())
                .departmentName(request.getDepartmentName())
                .doctorId(request.getDoctorId())
                .doctorName(request.getDoctorName())
                .status(VisitStatus.REGISTERED)
                .chiefComplaint(request.getChiefComplaint())
                .tokenNumber(tokenNumber)
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .followUp(request.getIsFollowUp() != null ? request.getIsFollowUp() : false)
                .createdBy(createdBy)
                .build();

        visit = visitRepository.save(visit);

        log.info("Visit created successfully: {}", visitNumber);
        return visitMapper.toResponse(visit);
    }

    @Override
    public VisitResponse updateVisitStatus(UUID uuid, VisitStatus status, Long updatedBy) {
        log.info("Updating visit status: {} to {}", uuid, status);

        PatientVisit visit = visitRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found"));

        visit.setStatus(status);
        visit.setUpdatedBy(updatedBy);

        // Set timestamps based on status
        if (status == VisitStatus.IN_CONSULTATION) {
            visit.setConsultationStartTime(Instant.now());
        } else if (status == VisitStatus.COMPLETED) {
            visit.setConsultationEndTime(Instant.now());
        }

        visit = visitRepository.save(visit);

        log.info("Visit status updated successfully: {}", uuid);
        return visitMapper.toResponse(visit);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VisitResponse> getVisitByUuid(UUID uuid) {
        return visitRepository.findByUuid(uuid)
                .map(visitMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VisitResponse> getVisitByNumber(String visitNumber) {
        return visitRepository.findByVisitNumber(visitNumber)
                .map(visitMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getVisitsByPatientUuid(UUID patientUuid) {
        return visitRepository.findByPatientUuidOrderByVisitDateDesc(patientUuid)
                .stream()
                .map(visitMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VisitResponse> getVisitsByPatientId(Long patientId, Pageable pageable) {
        return visitRepository.findByPatientId(patientId, pageable)
                .map(visitMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VisitResponse> getTodayVisitsByDepartment(Long departmentId, Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        return visitRepository.findByDepartmentAndDateRange(departmentId, startOfDay, endOfDay, pageable)
                .map(visitMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getDepartmentQueue(Long departmentId) {
        List<VisitStatus> queueStatuses = List.of(
                VisitStatus.IN_QUEUE,
                VisitStatus.IN_CONSULTATION
        );

        return visitRepository.findQueueByDepartment(departmentId, queueStatuses)
                .stream()
                .map(visitMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getVisitsByDoctorAndStatus(Long doctorId, VisitStatus status) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        return visitRepository.findByDoctorAndStatusAndDate(doctorId, status, startOfDay)
                .stream()
                .map(visitMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Integer getNextTokenNumber(Long departmentId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        long count = visitRepository.countByDepartmentAndDateRange(departmentId, startOfDay, endOfDay);
        return (int) (count + 1);
    }

    @Override
    public VisitResponse checkInPatient(UUID uuid) {
        log.info("Checking in patient for visit: {}", uuid);

        PatientVisit visit = visitRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found"));

        visit.setStatus(VisitStatus.IN_QUEUE);
        visit.setCheckInTime(Instant.now());

        visit = visitRepository.save(visit);

        log.info("Patient checked in successfully: {}", uuid);
        return visitMapper.toResponse(visit);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VisitResponse> getAllVisits(Pageable pageable) {
        return visitRepository.findAll(pageable)
                .map(visitMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getVisitsByStatus(VisitStatus status) {
        return visitRepository.findByStatus(status)
                .stream()
                .map(visitMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Generates a unique visit number.
     * Format: VNYYYYMMDDXXXX where YYYYMMDD is date, XXXX is sequential number
     */
    private String generateVisitNumber() {
        String datePart = LocalDate.now().format(VISIT_DATE_FORMAT);
        long sequence = (System.currentTimeMillis() % 10000);
        return String.format("%s%s%04d", VISIT_NUMBER_PREFIX, datePart, sequence);
    }
}
