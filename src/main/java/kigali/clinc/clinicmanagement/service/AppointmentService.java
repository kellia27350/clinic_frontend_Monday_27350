package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    // A4
    public String saveAppointment(Appointment appointment) {
        boolean booked = appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(), appointment.getAppointmentDate(), AppointmentStatus.CANCELLED);
        if (booked) {
            return "Doctor is already booked on that date";
        }
        appointmentRepo.save(appointment);
        return "Appointment is saved Successfully";
    }

    public List<Appointment> getAll() {
        return appointmentRepo.findAll();
    }

    // A2
    public List<Appointment> getByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    // A3
    public List<Appointment> getBetween(LocalDate start, LocalDate end) {
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    // C1
    public List<Object[]> countByStatus() {
        return appointmentRepo.countByStatus();
    }

    // C4
    @Transactional
    public String cancelDay(UUID doctorId, LocalDate date) {
        return appointmentRepo.cancelDay(doctorId, date) + " appointments cancelled";
    }

    // bonus
    public Page<Appointment> getPage(Pageable pageable) {
        return appointmentRepo.findAll(pageable);
    }

    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        return appointmentRepo.deleteCancelledBefore(date) + " appointments deleted";
    }
}
