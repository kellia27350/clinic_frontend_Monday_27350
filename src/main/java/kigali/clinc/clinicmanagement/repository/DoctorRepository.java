package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kigali.clinc.clinicmanagement.domain.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    List<Doctor> findByFirstNameAndLastName(String firstName, String lastName);

    // B1
    @Query("select d from Doctor d join d.specializations s where lower(s.name) = lower(:name)")
    List<Doctor> findBySpecialization(@Param("name") String name);

    // B2
    @Query("select d from Doctor d where d.office is null order by d.lastName")
    List<Doctor> findWithoutOffice();
}
