package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kigali.clinc.clinicmanagement.domain.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    List<Doctor> findByFirstNameAndLastName(String firstName, String lastName);

    // Quiz 1 B1 — doctors by specialization name (case-insensitive)
    @Query("select d from Doctor d join d.specializations s where lower(s.name) = lower(:name)")
    List<Doctor> findBySpecialization(@Param("name") String name);

    // Quiz 1 B2 — doctors without an office
    @Query("select d from Doctor d where d.office is null order by d.lastName")
    List<Doctor> findWithoutOffice();

    // A4 — check if any doctor already occupies a given office number (DERIVED)
    boolean existsByOfficeOfficeNumber(int officeNumber);

    // D3 — search doctors by first or last name, case-insensitive, sorted by last name (JPQL)
    @Query("""
        select d from Doctor d
        where lower(d.firstName) like lower(concat('%', :name, '%'))
           or lower(d.lastName)  like lower(concat('%', :name, '%'))
        order by d.lastName
        """)
    List<Doctor> searchByName(@Param("name") String name);
}
