package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kigali.clinc.clinicmanagement.domain.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    // C2 — prescriptions for one appointment (DERIVED)
    List<Prescription> findByAppointmentId(UUID appointmentId);

    // C4 — check if an appointment has any prescriptions before deleting it (DERIVED)
    boolean existsByAppointmentId(UUID appointmentId);

    // C3 — all prescriptions of a patient across appointments, newest first (JPQL)
    @Query("""
        select p from Prescription p
        where p.appointment.patient.ssNumber = :ssNumber
        order by p.issuedDate desc
        """)
    List<Prescription> findByPatientSsNumber(@Param("ssNumber") String ssNumber);

    // Bonus +5 — most prescribed medicines (JPQL GROUP BY)
    @Query("""
        select p.medicineName, count(p)
        from Prescription p
        group by p.medicineName
        order by count(p) desc
        """)
    List<Object[]> countByMedicine();
}
