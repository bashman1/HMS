package com.hms.patient.domain.port;

import com.hms.patient.domain.model.entity.PatientVisit;
import com.hms.patient.domain.model.entity.PatientVisit.VisitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for PatientVisit entity.
 */
@Repository
public interface PatientVisitRepository extends JpaRepository<PatientVisit, Long> {

    Optional<PatientVisit> findByUuid(UUID uuid);

    Optional<PatientVisit> findByVisitNumber(String visitNumber);

    List<PatientVisit> findByPatientIdOrderByVisitDateDesc(Long patientId);

    Page<PatientVisit> findByPatientId(Long patientId, Pageable pageable);

    @Query("SELECT v FROM PatientVisit v WHERE v.patient.uuid = :patientUuid ORDER BY v.visitDate DESC")
    List<PatientVisit> findByPatientUuidOrderByVisitDateDesc(@Param("patientUuid") UUID patientUuid);

    @Query("SELECT v FROM PatientVisit v WHERE v.departmentId = :departmentId AND " +
            "v.visitDate BETWEEN :startDate AND :endDate ORDER BY v.visitDate DESC")
    Page<PatientVisit> findByDepartmentAndDateRange(@Param("departmentId") Long departmentId,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate,
                                                     Pageable pageable);

    @Query("SELECT v FROM PatientVisit v WHERE v.doctorId = :doctorId AND " +
            "v.status = :status AND v.visitDate >= :date ORDER BY v.tokenNumber ASC")
    List<PatientVisit> findByDoctorAndStatusAndDate(@Param("doctorId") Long doctorId,
                                                     @Param("status") VisitStatus status,
                                                     @Param("date") LocalDateTime date);

    @Query("SELECT COUNT(v) FROM PatientVisit v WHERE v.departmentId = :departmentId AND " +
            "v.visitDate BETWEEN :startDate AND :endDate")
    long countByDepartmentAndDateRange(@Param("departmentId") Long departmentId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT v FROM PatientVisit v WHERE v.status IN :statuses AND " +
            "v.departmentId = :departmentId ORDER BY v.priority DESC, v.tokenNumber ASC")
    List<PatientVisit> findQueueByDepartment(@Param("departmentId") Long departmentId,
                                               @Param("statuses") List<VisitStatus> statuses);

    @Query("SELECT v FROM PatientVisit v WHERE v.visitDate >= :startDate AND v.visitDate < :endDate " +
            "ORDER BY v.visitDate DESC")
    Page<PatientVisit> findTodayVisits(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);

    boolean existsByVisitNumber(String visitNumber);

    List<PatientVisit> findByStatus(VisitStatus status);
}
