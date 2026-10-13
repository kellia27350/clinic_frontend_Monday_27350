package kigali.clinc.clinicmanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.dto.AppointmentDetailsDTO;
import kigali.clinc.clinicmanagement.dto.DoctorAppointmentCountDTO;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // ---- existing ----
    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateAsc(UUID doctorId, AppointmentStatus status);

    Page<Appointment> findByDoctorIdOrderByAppointmentDateAsc(UUID doctorId, Pageable pageable);

    @Query("""
        select new kigali.clinc.clinicmanagement.dto.AppointmentDetailsDTO(
            concat(p.firstName, ' ', p.lastName), concat(d.firstName, ' ', d.lastName), a.appointmentDate)
        from Appointment a join a.patient p join a.doctor d
        where a.status = :status and a.appointmentDate < :date
        order by a.appointmentDate asc
        """)
    List<AppointmentDetailsDTO> findPendingPastAppointmentsAsDto(@Param("status") AppointmentStatus status,
                                                                 @Param("date") LocalDate date);

    @Query("""
        select new kigali.clinc.clinicmanagement.dto.DoctorAppointmentCountDTO(a.doctor.id, count(a))
        from Appointment a group by a.doctor.id order by count(a) desc
        """)
    List<DoctorAppointmentCountDTO> countAppointmentsPerDoctor();

    @Query("""
        select a from Appointment a join a.doctor d join d.specializations s
        where s.name = :specialization order by a.appointmentDate asc
        """)
    List<Appointment> findByDoctorSpecializationName(@Param("specialization") String specialization);

    // ---- A: derived ----
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(LocalDate start, LocalDate end);

    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, LocalDate date, AppointmentStatus status);

    // ---- C: aggregates and bulk ----
    @Query("select a.status, count(a) from Appointment a group by a.status")
    List<Object[]> countByStatus();

    @Modifying
    @Query("""
        update Appointment a
        set a.status = kigali.clinc.clinicmanagement.domain.AppointmentStatus.CANCELLED
        where a.doctor.id = :doctorId and a.appointmentDate = :date
        and a.status <> kigali.clinc.clinicmanagement.domain.AppointmentStatus.COMPLETED
        """)
    int cancelDay(@Param("doctorId") UUID doctorId, @Param("date") LocalDate date);

    // ---- bonus ----
    @Modifying
    @Query("""
        delete from Appointment a
        where a.status = kigali.clinc.clinicmanagement.domain.AppointmentStatus.CANCELLED
        and a.appointmentDate < :date
        """)
    int deleteCancelledBefore(@Param("date") LocalDate date);

    // D1 — doctor's appointments on a date, without CANCELLED ones (DERIVED)
    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(
        UUID doctorId, LocalDate date, AppointmentStatus status);

    // D2 — patient's upcoming appointments, SCHEDULED or CONFIRMED, soonest first (DERIVED)
    List<Appointment> findByPatientSsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
        String ssNumber, LocalDate date, List<AppointmentStatus> statuses);

    // Bonus — doctor workload: COMPLETED appointments per doctor between two dates (JPQL)
    @Query("""
        select d.firstName, d.lastName, count(a)
        from Appointment a join a.doctor d
        where a.status = kigali.clinc.clinicmanagement.domain.AppointmentStatus.COMPLETED
          and a.appointmentDate between :start and :end
        group by d.id, d.firstName, d.lastName
        order by count(a) desc
        """)
    List<Object[]> countWorkload(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
