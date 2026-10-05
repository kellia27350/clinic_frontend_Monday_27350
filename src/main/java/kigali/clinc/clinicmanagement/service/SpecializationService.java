package kigali.clinc.clinicmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specRepo;

    public String saveSpecialization(Specialization spec) {
        if (specRepo.findByName(spec.getName()).isPresent()) {
            return "Specialization " + spec.getName() + " already exists";
        }
        specRepo.save(spec);
        return "Specialization is saved Successfully";
    }

    public List<Specialization> getAll() {
        return specRepo.findAll();
    }

    public List<Specialization> getUnused() {
        return specRepo.findUnused();
    }
}
