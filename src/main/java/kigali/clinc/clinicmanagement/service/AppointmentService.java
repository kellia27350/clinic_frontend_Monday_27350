package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    @Autowired private AppointmentRepository appointmentRepo;
    @Autowired private PrescriptionRepository prescriptionRepo;
    @Autowired private PatientRepository      patientRepo;

    // B1 — reject past dates, force SCHEDULED, block double-booking
    public String saveAppointment(Appointment appointment) {
        if (appointment.getAppointmentDate().isBefore(LocalDate.now()))
            return "Appointment date cannot be in the past";

        appointment.setStatus(AppointmentStatus.SCHEDULED);

        boolean doctorBusy = appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(),
                appointment.getAppointmentDate(),
                AppointmentStatus.CANCELLED);
        if (doctorBusy) return "Doctor is already booked on that date";

        appointmentRepo.save(appointment);
        return "Appointment is saved Successfully";
    }

    public List<Appointment> getAll()                                        { return appointmentRepo.findAll(); }
    public List<Appointment> getByStatus(AppointmentStatus s)               { return appointmentRepo.findByStatusOrderByAppointmentDateAsc(s); }
    public List<Appointment> getBetween(LocalDate start, LocalDate end)     { return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end); }
    public List<Object[]>    countByStatus()                                 { return appointmentRepo.countByStatus(); }
    public Page<Appointment> getPage(Pageable p)                             { return appointmentRepo.findAll(p); }

    @Transactional
    public String cancelDay(UUID doctorId, LocalDate date) {
        return appointmentRepo.cancelDay(doctorId, date) + " appointments cancelled";
    }

    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        return appointmentRepo.deleteCancelledBefore(date) + " appointments deleted";
    }

    // B2 — only allowed status transitions: SCHEDULED→CONFIRMED/CANCELLED, CONFIRMED→COMPLETED/CANCELLED
    public String updateStatus(UUID id, AppointmentStatus newStatus) {
        Optional<Appointment> found = appointmentRepo.findById(id);
        if (found.isEmpty()) return "The appointment with that id does not exist";

        Appointment apt     = found.get();
        AppointmentStatus current = apt.getStatus();

        boolean allowed =
            (current == AppointmentStatus.SCHEDULED  && (newStatus == AppointmentStatus.CONFIRMED  || newStatus == AppointmentStatus.CANCELLED)) ||
            (current == AppointmentStatus.CONFIRMED   && (newStatus == AppointmentStatus.COMPLETED  || newStatus == AppointmentStatus.CANCELLED));

        if (!allowed) return "Cannot change status from " + current + " to " + newStatus;

        apt.setStatus(newStatus);
        appointmentRepo.save(apt);
        return "Appointment status updated successfully";
    }

    // B3 — change date, reset to SCHEDULED, block if doctor is already busy that day
    public String reschedule(UUID id, LocalDate newDate) {
        Optional<Appointment> found = appointmentRepo.findById(id);
        if (found.isEmpty()) return "The appointment with that id does not exist";

        Appointment apt = found.get();
        if (apt.getStatus() == AppointmentStatus.COMPLETED) return "A COMPLETED appointment cannot be rescheduled";
        if (apt.getStatus() == AppointmentStatus.CANCELLED)  return "A CANCELLED appointment cannot be rescheduled";
        if (newDate.isBefore(LocalDate.now()))               return "Appointment date cannot be in the past";

        boolean doctorBusy = appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                apt.getDoctor().getId(), newDate, AppointmentStatus.CANCELLED);
        if (doctorBusy) return "Doctor is already booked on that date";

        apt.setAppointmentDate(newDate);
        apt.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(apt);
        return "Appointment rescheduled successfully";
    }

    // C4 — block deletion if the appointment already has prescriptions
    public String deleteAppointment(UUID id) {
        if (!appointmentRepo.existsById(id))         return "The appointment with that id does not exist";
        if (prescriptionRepo.existsByAppointmentId(id)) return "That appointment has prescriptions, delete them first";
        appointmentRepo.deleteById(id);
        return "Appointment deleted successfully";
    }

    // D2 — patient's upcoming SCHEDULED or CONFIRMED appointments, soonest first
    public Optional<List<Appointment>> getUpcoming(String ssNumber) {
        if (patientRepo.findBySsNumber(ssNumber).isEmpty()) return Optional.empty();
        List<AppointmentStatus> active = List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);
        return Optional.of(
            appointmentRepo.findByPatientSsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                ssNumber, LocalDate.now(), active));
    }
}
