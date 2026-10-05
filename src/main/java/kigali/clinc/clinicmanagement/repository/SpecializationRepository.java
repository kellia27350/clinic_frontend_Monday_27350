package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kigali.clinc.clinicmanagement.domain.Specialization;

public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {

    Optional<Specialization> findByName(String name);

    // B3
    @Query("select s from Specialization s where s.doctors is empty")
    List<Specialization> findUnused();
}
