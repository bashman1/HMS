package com.hms.patient.infrastructure.web.controller;

import com.hms.patient.application.PatientService;
import com.hms.patient.application.dto.request.PatientRegistrationRequest;
import com.hms.patient.application.dto.request.PatientUpdateRequest;
import com.hms.patient.application.dto.response.PatientResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for patient operations.
 */
@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Patient Management", description = "Patient registration and management APIs")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @Operation(summary = "Register a new patient")
    public ResponseEntity<PatientResponse> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long createdBy = getUserId(userDetails);
        log.info("Registering new patient: {} {}", request.getFirstName(), request.getLastName());

        PatientResponse response = patientService.registerPatient(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get patient by UUID")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable UUID uuid) {
        return patientService.getPatientByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/uhid/{uhid}")
    @Operation(summary = "Get patient by UHID")
    public ResponseEntity<PatientResponse> getPatientByUhid(@PathVariable String uhid) {
        return patientService.getPatientByUhid(uhid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Search patients")
    public ResponseEntity<Page<PatientResponse>> searchPatients(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<PatientResponse> patients;
        if (query != null && !query.isEmpty()) {
            patients = patientService.searchPatients(query, pageable);
        } else {
            patients = patientService.getAllActivePatients(pageable);
        }
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update patient")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable UUID uuid,
            @Valid @RequestBody PatientUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long updatedBy = getUserId(userDetails);
        log.info("Updating patient: {}", uuid);

        PatientResponse response = patientService.updatePatient(uuid, request, updatedBy);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}/deactivate")
    @Operation(summary = "Deactivate patient")
    public ResponseEntity<Void> deactivatePatient(
            @PathVariable UUID uuid,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long updatedBy = getUserId(userDetails);
        log.info("Deactivating patient: {}", uuid);

        patientService.deactivatePatient(uuid, updatedBy);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/activate")
    @Operation(summary = "Activate patient")
    public ResponseEntity<Void> activatePatient(
            @PathVariable UUID uuid,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long updatedBy = getUserId(userDetails);
        log.info("Activating patient: {}", uuid);

        patientService.activatePatient(uuid, updatedBy);
        return ResponseEntity.ok().build();
    }

    /**
     * Extracts user ID from UserDetails.
     * In a real implementation, this would get the user ID from the JWT claims.
     */
    private Long getUserId(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        try {
            return Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
