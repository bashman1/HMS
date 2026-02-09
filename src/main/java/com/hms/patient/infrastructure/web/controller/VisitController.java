package com.hms.patient.infrastructure.web.controller;

import com.hms.patient.application.VisitService;
import com.hms.patient.application.dto.request.CreateVisitRequest;
import com.hms.patient.application.dto.response.VisitResponse;
import com.hms.patient.domain.model.entity.PatientVisit.VisitStatus;
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

import java.util.List;
import java.util.UUID;

/**
 * REST controller for patient visit (OPD) operations.
 */
@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OPD Management", description = "Outpatient Department visit APIs")
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    @Operation(summary = "Create a new patient visit")
    public ResponseEntity<VisitResponse> createVisit(
            @Valid @RequestBody CreateVisitRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long createdBy = getUserId(userDetails);
        log.info("Creating new visit for patient: {}", request.getPatientId());

        VisitResponse response = visitService.createVisit(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get visit by UUID")
    public ResponseEntity<VisitResponse> getVisit(@PathVariable UUID uuid) {
        return visitService.getVisitByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{visitNumber}")
    @Operation(summary = "Get visit by visit number")
    public ResponseEntity<VisitResponse> getVisitByNumber(@PathVariable String visitNumber) {
        return visitService.getVisitByNumber(visitNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientUuid}/visits")
    @Operation(summary = "Get visits by patient UUID")
    public ResponseEntity<List<VisitResponse>> getPatientVisits(@PathVariable UUID patientUuid) {
        List<VisitResponse> visits = visitService.getVisitsByPatientUuid(patientUuid);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get visits by patient ID with pagination")
    public ResponseEntity<Page<VisitResponse>> getPatientVisitsById(
            @PathVariable Long patientId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<VisitResponse> visits = visitService.getVisitsByPatientId(patientId, pageable);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/department/{departmentId}/today")
    @Operation(summary = "Get today's visits for a department")
    public ResponseEntity<Page<VisitResponse>> getTodayVisitsByDepartment(
            @PathVariable Long departmentId,
            @PageableDefault(size = 50) Pageable pageable) {
        Page<VisitResponse> visits = visitService.getTodayVisitsByDepartment(departmentId, pageable);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/department/{departmentId}/queue")
    @Operation(summary = "Get queue for a department")
    public ResponseEntity<List<VisitResponse>> getDepartmentQueue(@PathVariable Long departmentId) {
        List<VisitResponse> queue = visitService.getDepartmentQueue(departmentId);
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/doctor/{doctorId}/visits")
    @Operation(summary = "Get visits by doctor and status")
    public ResponseEntity<List<VisitResponse>> getDoctorVisits(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "IN_QUEUE") VisitStatus status) {
        List<VisitResponse> visits = visitService.getVisitsByDoctorAndStatus(doctorId, status);
        return ResponseEntity.ok(visits);
    }

    @GetMapping
    @Operation(summary = "Get all visits with pagination")
    public ResponseEntity<Page<VisitResponse>> getAllVisits(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<VisitResponse> visits = visitService.getAllVisits(pageable);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get visits by status")
    public ResponseEntity<List<VisitResponse>> getVisitsByStatus(@PathVariable VisitStatus status) {
        List<VisitResponse> visits = visitService.getVisitsByStatus(status);
        return ResponseEntity.ok(visits);
    }

    @PatchMapping("/{uuid}/status")
    @Operation(summary = "Update visit status")
    public ResponseEntity<VisitResponse> updateVisitStatus(
            @PathVariable UUID uuid,
            @RequestParam VisitStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long updatedBy = getUserId(userDetails);
        log.info("Updating visit status: {} to {}", uuid, status);

        VisitResponse response = visitService.updateVisitStatus(uuid, status, updatedBy);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{uuid}/check-in")
    @Operation(summary = "Check in patient for visit")
    public ResponseEntity<VisitResponse> checkInPatient(@PathVariable UUID uuid) {
        log.info("Checking in patient for visit: {}", uuid);

        VisitResponse response = visitService.checkInPatient(uuid);
        return ResponseEntity.ok(response);
    }

    /**
     * Extracts user ID from UserDetails.
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
