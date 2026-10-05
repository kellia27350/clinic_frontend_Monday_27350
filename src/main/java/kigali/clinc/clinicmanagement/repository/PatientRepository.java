package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kigali.clinc.clinicmanagement.domain.Patient;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    
    List<Patient> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);

   
    @Query("select distinct p from Patient p join p.appointments a where a.doctor.id = :doctorId")
    List<Patient> findByDoctor(@Param("doctorId") UUID doctorId);

   
    @Query("select p from Patient p join p.appointments a group by p having count(a) >= :min order by count(a) desc")
    List<Patient> findFrequent(@Param("min") long min);
}
