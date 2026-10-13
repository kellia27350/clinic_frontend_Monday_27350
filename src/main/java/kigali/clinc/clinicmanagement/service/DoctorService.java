package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import kigali.clinc.clinicmanagement.domain.*;
import kigali.clinc.clinicmanagement.repository.*;

@Service
public class DoctorService {

    @Autowired private DoctorRepository       doctorRepo;
    @Autowired private SpecializationRepository specRepo;
    @Autowired private OfficeRepository       officeRepo;
    @Autowired private AppointmentRepository  appointmentRepo;

    public String saveDoctor(Doctor doctor) {
        doctorRepo.save(doctor);
        return "Doctor saved successfully";
    }

    public List<Doctor> getAll()                         { return doctorRepo.findAll(); }
    public List<Doctor> getBySpecialization(String name) { return doctorRepo.findBySpecialization(name); }
    public List<Doctor> getWithoutOffice()               { return doctorRepo.findWithoutOffice(); }
    public List<Doctor> searchByName(String name)        { return doctorRepo.searchByName(name); }

    // A1 — add one specialization to a doctor
    public String addSpecialization(UUID doctorId, UUID specId) {
        Optional<Doctor> doc = doctorRepo.findById(doctorId);
        if (doc.isEmpty()) return "The doctor with that id does not exist";

        Optional<Specialization> spec = specRepo.findById(specId);
        if (spec.isEmpty()) return "That specialization does not exist";

        Doctor doctor = doc.get();
        if (doctor.getSpecializations().contains(spec.get()))
            return "The doctor already has that specialization";

        doctor.getSpecializations().add(spec.get());
        doctorRepo.save(doctor);
        return "Specialization added to the doctor";
    }

    // A2 — remove one specialization from a doctor (link only, specialization stays)
    public String removeSpecialization(UUID doctorId, UUID specId) {
        Optional<Doctor> doc = doctorRepo.findById(doctorId);
        if (doc.isEmpty()) return "The doctor with that id does not exist";

        Optional<Specialization> spec = specRepo.findById(specId);
        if (spec.isEmpty()) return "That specialization does not exist";

        Doctor doctor = doc.get();
        if (!doctor.getSpecializations().contains(spec.get()))
            return "The doctor does not have that specialization";

        doctor.getSpecializations().remove(spec.get());
        doctorRepo.save(doctor);
        return "Specialization removed from the doctor";
    }

    // A4 — move a doctor to a different office
    public String moveToOffice(UUID doctorId, int officeNumber) {
        Optional<Doctor> doc = doctorRepo.findById(doctorId);
        if (doc.isEmpty()) return "The doctor with that id does not exist";

        Office office = officeRepo.findByOfficeNumber(officeNumber);
        if (office == null) return "The office with that office number does not exist";

        if (doctorRepo.existsByOfficeOfficeNumber(officeNumber))
            return "That office is taken by other doctor";

        Doctor doctor = doc.get();
        doctor.setOffice(office);
        doctorRepo.save(doctor);
        return "Office changed successfully";
    }

    // D1 — doctor's appointments on a specific day, CANCELLED ones excluded
    public Optional<List<Appointment>> getAgenda(UUID doctorId, LocalDate date) {
        if (!doctorRepo.existsById(doctorId)) return Optional.empty();
        return Optional.of(appointmentRepo.findByDoctorIdAndAppointmentDateAndStatusNot(
                doctorId, date, AppointmentStatus.CANCELLED));
    }

    // Bonus — how many COMPLETED appointments each doctor had in a date range
    public List<Object[]> getWorkload(LocalDate start, LocalDate end) {
        return appointmentRepo.countWorkload(start, end);
    }
}
