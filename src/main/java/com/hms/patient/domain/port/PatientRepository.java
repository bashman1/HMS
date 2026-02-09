package com.hms.patient.domain.port;

import com.hms.patient.domain.model.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Patient entity.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUuid(UUID uuid);

    Optional<Patient> findByUhid(String uhid);

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByPhonePrimary(String phonePrimary);

    boolean existsByUhid(String uhid);

    boolean existsByEmail(String email);

    boolean existsByPhonePrimary(String phonePrimary);

    @Query("SELECT p FROM Patient p WHERE p.active = true AND " +
            "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.middleName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "p.phonePrimary LIKE CONCAT('%', :query, '%') OR " +
            "p.uhid LIKE CONCAT('%', :query, '%'))")
    Page<Patient> searchPatients(@Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.active = true AND " +
            "(p.phonePrimary = :phone OR p.email = :email OR p.uhid = :uhid)")
    Optional<Patient> findByPhoneOrEmailOrUhid(@Param("phone") String phone,
                                                 @Param("email") String email,
                                                 @Param("uhid") String uhid);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.active = true")
    long countActivePatients();

    @Query("SELECT p FROM Patient p WHERE p.active = true")
    Page<Patient> findByActiveTrue(Pageable pageable);
}
