package kigali.clinc.clinicmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepo;

    public String saveDoctor(Doctor doctor) {
        doctorRepo.save(doctor);
        return "Doctor is saved Successfully";
    }

    public List<Doctor> getAll() {
        return doctorRepo.findAll();
    }

    public List<Doctor> getBySpecialization(String name) {
        return doctorRepo.findBySpecialization(name);
    }

    public List<Doctor> getWithoutOffice() {
        return doctorRepo.findWithoutOffice();
    }
}
