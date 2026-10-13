package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.repository.*;

@Service
public class DashboardService {

    @Autowired private PatientRepository      patientRepo;
    @Autowired private DoctorRepository       doctorRepo;
    @Autowired private OfficeRepository       officeRepo;
    @Autowired private AppointmentRepository  appointmentRepo;
    @Autowired private PrescriptionRepository prescriptionRepo;

    // D4 — one-stop clinic summary
    public Map<String, Long> getSummary() {
        long freeOffices       = officeRepo.findFreeOffices().size();
        long appointmentsToday = appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                                     LocalDate.now(), LocalDate.now()).size();
        long scheduled         = appointmentRepo.findByStatusOrderByAppointmentDateAsc(
                                     AppointmentStatus.SCHEDULED).size();

        // LinkedHashMap keeps the keys in insertion order in the JSON response
        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("patients",          patientRepo.count());
        summary.put("doctors",           doctorRepo.count());
        summary.put("freeOffices",       freeOffices);
        summary.put("appointmentsToday", appointmentsToday);
        summary.put("scheduled",         scheduled);
        summary.put("prescriptions",     prescriptionRepo.count());
        return summary;
    }
}
