package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired private PatientRepository patientRepo;
    @Autowired private DoctorRepository  doctorRepo;

    public String savePatient(Patient patient) {
        patientRepo.save(patient);
        return "Patient is saved Successfully";
    }

    public List<Patient> getAll() {
        return patientRepo.findAll();
    }

    public List<Patient> getByLastName(String lastName) {
        return patientRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    // returns empty Optional if the doctor doesn't exist
    public Optional<List<Patient>> getOfDoctor(UUID doctorId) {
        if (!doctorRepo.existsById(doctorId)) return Optional.empty();
        return Optional.of(patientRepo.findByDoctor(doctorId));
    }

    public List<Patient> getFrequent(long min) {
        return patientRepo.findFrequent(min);
    }
}
