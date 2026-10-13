package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired private PrescriptionRepository prescriptionRepo;
    @Autowired private AppointmentRepository  appointmentRepo;

    // C1 — only COMPLETED appointments can have prescriptions
    public String save(UUID appointmentId, Prescription prescription) {
        Optional<Appointment> found = appointmentRepo.findById(appointmentId);
        if (found.isEmpty()) return "The appointment with that id does not exist";

        Appointment apt = found.get();
        if (apt.getStatus() != AppointmentStatus.COMPLETED)
            return "Prescriptions are only for COMPLETED appointments";

        prescription.setIssuedDate(LocalDate.now());
        prescription.setAppointment(apt);
        prescriptionRepo.save(prescription);
        return "Prescription saved successfully";
    }

    // C2 — list prescriptions for a given appointment
    public Optional<List<Prescription>> getByAppointment(UUID appointmentId) {
        if (!appointmentRepo.existsById(appointmentId)) return Optional.empty();
        return Optional.of(prescriptionRepo.findByAppointmentId(appointmentId));
    }

    // C3 — full prescription history for a patient, newest first
    public List<Prescription> getByPatient(String ssNumber) {
        return prescriptionRepo.findByPatientSsNumber(ssNumber);
    }

    // C4 — update medicine, dosage and duration (other fields stay unchanged)
    public String update(UUID id, Prescription body) {
        Optional<Prescription> found = prescriptionRepo.findById(id);
        if (found.isEmpty()) return "The prescription with that id does not exist";

        Prescription p = found.get();
        p.setMedicineName(body.getMedicineName());
        p.setDosage(body.getDosage());
        p.setDurationDays(body.getDurationDays());
        prescriptionRepo.save(p);
        return "Prescription updated successfully";
    }

    // C4 — delete a prescription
    public String delete(UUID id) {
        if (!prescriptionRepo.existsById(id)) return "The prescription with that id does not exist";
        prescriptionRepo.deleteById(id);
        return "Prescription deleted successfully";
    }

    // Bonus — medicines ranked by how often they were prescribed
    public List<Object[]> topMedicines() {
        return prescriptionRepo.countByMedicine();
    }
}
