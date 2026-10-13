package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Office;

@Repository
public interface OfficeRepository extends  JpaRepository<Office,UUID >{
    
    Office findByOfficeNumber(int officeNumber);

    Optional<Office> findByNameAndOfficeNumber(String name, int officeNumber);

    Boolean existsByOfficeNumber(int officeNumber);

    // C3: name, number, appointment count — pass PageRequest.of(0,1) for the top row
    @Query("""
        select o.name, o.officeNumber, count(a)
        from Appointment a join a.doctor d join d.office o
        group by o.name, o.officeNumber
        order by count(a) desc
        """)
    List<Object[]> findBusiest(Pageable top);

    // A3 — offices with no doctor, sorted by office number (JPQL NOT EXISTS)
    @Query("""
        select o from Office o
        where not exists (select d from Doctor d where d.office = o)
        order by o.officeNumber
        """)
    List<Office> findFreeOffices();
}
