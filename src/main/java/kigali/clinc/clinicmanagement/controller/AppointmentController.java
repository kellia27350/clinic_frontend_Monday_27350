package kigali.clinc.clinicmanagement.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kigali.clinc.clinicmanagement.domain.*;
import kigali.clinc.clinicmanagement.dto.*;
import kigali.clinc.clinicmanagement.repository.*;
import kigali.clinc.clinicmanagement.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired private AppointmentService    appointmentService;
    @Autowired private AppointmentRepository appointmentRepo;
    @Autowired private DoctorRepository      doctorRepo;

    // B1 — reject past dates, force SCHEDULED, block double-booking
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Appointment appointment) {
        String msg = appointmentService.saveAppointment(appointment);
        if (msg.contains("past"))   return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        if (msg.contains("booked")) return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<Appointment> all() { return appointmentService.getAll(); }

    @GetMapping("/by-status")
    public List<Appointment> byStatus(@RequestParam AppointmentStatus status) {
        return appointmentService.getByStatus(status);
    }

    @GetMapping("/between")
    public List<Appointment> between(@RequestParam String start, @RequestParam String end) {
        return appointmentService.getBetween(LocalDate.parse(start), LocalDate.parse(end));
    }

    @GetMapping("/stats/by-status")
    public List<Object[]> statsByStatus() { return appointmentService.countByStatus(); }

    @PatchMapping("/cancel-day")
    public String cancelDay(@RequestParam UUID doctorId, @RequestParam String date) {
        return appointmentService.cancelDay(doctorId, LocalDate.parse(date));
    }

    @GetMapping("/page")
    public Page<Appointment> page(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  @RequestParam(defaultValue = "appointmentDate,asc") String sort) {
        String[] parts = sort.split(",");
        Sort.Direction dir = parts.length > 1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.ASC;
        return appointmentService.getPage(PageRequest.of(page, size, Sort.by(dir, parts[0])));
    }

    @DeleteMapping("/cancelled-before")
    public String cancelledBefore(@RequestParam String date) {
        return appointmentService.deleteCancelledBefore(LocalDate.parse(date));
    }

    // B2 — status flow: SCHEDULED→CONFIRMED/CANCELLED, CONFIRMED→COMPLETED/CANCELLED
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable UUID id,
                                               @RequestParam AppointmentStatus status) {
        String msg = appointmentService.updateStatus(id, status);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.contains("Cannot"))         return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    // B3 — change the date, reset to SCHEDULED, check doctor is free on that day
    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<String> reschedule(@PathVariable UUID id, @RequestParam String newDate) {
        String msg = appointmentService.reschedule(id, LocalDate.parse(newDate));
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.contains("cannot be"))      return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        if (msg.contains("past"))           return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        if (msg.contains("booked"))         return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    // C4 — delete appointment, blocked when it already has prescriptions
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String msg = appointmentService.deleteAppointment(id);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.contains("prescriptions"))  return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    // ── Quiz 1 endpoints kept intact ──────────────────────────────────────────

    @GetMapping("/pending/doctor/{doctorId}")
    public List<Appointment> pendingForDoctor(@PathVariable UUID doctorId) {
        return appointmentRepo.findByDoctorIdAndStatusOrderByAppointmentDateAsc(
                doctorId, AppointmentStatus.PENDING);
    }

    @GetMapping("/pending/doctor")
    public ResponseEntity<List<Appointment>> pendingByDoctorName(@RequestParam String firstName,
                                                                  @RequestParam String lastName) {
        List<Doctor> doctors = doctorRepo.findByFirstNameAndLastName(firstName, lastName);
        if (doctors.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(appointmentRepo.findByDoctorIdAndStatusOrderByAppointmentDateAsc(
                doctors.get(0).getId(), AppointmentStatus.PENDING));
    }

    @GetMapping("/pending/past")
    public List<AppointmentDetailsDTO> pendingPast(@RequestParam(required = false) String beforeDate) {
        LocalDate cutoff = beforeDate == null ? LocalDate.now() : LocalDate.parse(beforeDate);
        return appointmentRepo.findPendingPastAppointmentsAsDto(AppointmentStatus.PENDING, cutoff);
    }

    @GetMapping("/count-by-doctor")
    public List<DoctorAppointmentCountDTO> countByDoctor() {
        return appointmentRepo.countAppointmentsPerDoctor();
    }

    @GetMapping("/specialization/{name}")
    public List<Appointment> bySpecialization(@PathVariable String name) {
        return appointmentRepo.findByDoctorSpecializationName(name);
    }

    @GetMapping("/doctor/{doctorId}")
    public Page<Appointment> byDoctor(@PathVariable UUID doctorId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return appointmentRepo.findByDoctorIdOrderByAppointmentDateAsc(
                doctorId, PageRequest.of(page, size));
    }
}
