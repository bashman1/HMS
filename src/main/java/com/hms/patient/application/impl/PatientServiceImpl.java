package com.hms.patient.application.impl;

import com.hms.patient.application.PatientMapper;
import com.hms.patient.application.PatientService;
import com.hms.patient.application.dto.request.PatientRegistrationRequest;
import com.hms.patient.application.dto.request.PatientUpdateRequest;
import com.hms.patient.application.dto.response.PatientResponse;
import com.hms.patient.domain.model.entity.Patient;
import com.hms.patient.domain.port.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of PatientService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    private static final String UHID_PREFIX = "UH";
    private static final DateTimeFormatter UHID_DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd");

    @Override
    public PatientResponse registerPatient(PatientRegistrationRequest request, Long createdBy) {
        log.info("Registering new patient: {} {}", request.getFirstName(), request.getLastName());

        // Check for duplicate phone, email, or UHID
        if (patientRepository.existsByPhonePrimary(request.getPhonePrimary())) {
            throw new IllegalArgumentException("A patient with this phone number already exists");
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()
                && patientRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("A patient with this email already exists");
        }

        // Generate unique UHID
        String uhid = generateUhid();
        while (patientRepository.existsByUhid(uhid)) {
            uhid = generateUhid();
        }

        // Create and save patient
        Patient patient = patientMapper.toEntity(request, uhid, createdBy);
        patient = patientRepository.save(patient);

        log.info("Patient registered successfully with UHID: {}", uhid);
        return patientMapper.toResponse(patient);
    }

    @Override
    public PatientResponse updatePatient(UUID uuid, PatientUpdateRequest request, Long updatedBy) {
        log.info("Updating patient: {}", uuid);

        Patient patient = patientRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        // Check for duplicate phone if being updated
        if (request.getPhonePrimary() != null && !request.getPhonePrimary().equals(patient.getPhonePrimary())) {
            if (patientRepository.existsByPhonePrimary(request.getPhonePrimary())) {
                throw new IllegalArgumentException("A patient with this phone number already exists");
            }
        }

        // Check for duplicate email if being updated
        if (request.getEmail() != null && !request.getEmail().equals(patient.getEmail())) {
            if (patientRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("A patient with this email already exists");
            }
        }

        patientMapper.updateEntity(patient, request);
        patient.setUpdatedBy(updatedBy);
        patient = patientRepository.save(patient);

        log.info("Patient updated successfully: {}", uuid);
        return patientMapper.toResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatientResponse> getPatientByUuid(UUID uuid) {
        return patientRepository.findByUuid(uuid)
                .map(patientMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatientResponse> getPatientByUhid(String uhid) {
        return patientRepository.findByUhid(uhid)
                .map(patientMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponse> searchPatients(String query, Pageable pageable) {
        return patientRepository.searchPatients(query, pageable)
                .map(patientMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponse> getAllActivePatients(Pageable pageable) {
        return patientRepository.findByActiveTrue(pageable)
                .map(patientMapper::toResponse);
    }

    @Override
    public void deactivatePatient(UUID uuid, Long updatedBy) {
        log.info("Deactivating patient: {}", uuid);

        Patient patient = patientRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        patient.setActive(false);
        patient.setUpdatedBy(updatedBy);
        patientRepository.save(patient);

        log.info("Patient deactivated successfully: {}", uuid);
    }

    @Override
    public void activatePatient(UUID uuid, Long updatedBy) {
        log.info("Activating patient: {}", uuid);

        Patient patient = patientRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        patient.setActive(true);
        patient.setUpdatedBy(updatedBy);
        patientRepository.save(patient);

        log.info("Patient activated successfully: {}", uuid);
    }

    /**
     * Generates a unique UHID (Universal Health ID).
     * Format: UHYYMMDDXXXX where YY is year, MM is month, DD is day, XXXX is sequential number
     */
    private String generateUhid() {
        String datePart = LocalDate.now().format(UHID_DATE_FORMAT);
        long sequence = (System.currentTimeMillis() % 10000);
        return String.format("%s%s%04d", UHID_PREFIX, datePart, sequence);
    }
}
