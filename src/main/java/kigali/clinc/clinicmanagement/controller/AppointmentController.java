package kigali.clinc.clinicmanagement.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.dto.AppointmentDetailsDTO;
import kigali.clinc.clinicmanagement.dto.DoctorAppointmentCountDTO;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // A4
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Appointment appointment) {
        String message = appointmentService.saveAppointment(appointment);
        HttpStatus status = message.endsWith("Successfully") ? HttpStatus.CREATED : HttpStatus.CONFLICT;
        return new ResponseEntity<>(message, status);
    }

    @GetMapping("/all")
    public List<Appointment> all() {
        return appointmentService.getAll();
    }

    // A2
    @GetMapping("/by-status")
    public List<Appointment> byStatus(@RequestParam AppointmentStatus status) {
        return appointmentService.getByStatus(status);
    }

    // A3
    @GetMapping("/between")
    public List<Appointment> between(@RequestParam String start, @RequestParam String end) {
        return appointmentService.getBetween(LocalDate.parse(start), LocalDate.parse(end));
    }

    // C1
    @GetMapping("/stats/by-status")
    public List<Object[]> statsByStatus() {
        return appointmentService.countByStatus();
    }

    // C4
    @PatchMapping("/cancel-day")
    public String cancelDay(@RequestParam UUID doctorId, @RequestParam String date) {
        return appointmentService.cancelDay(doctorId, LocalDate.parse(date));
    }

    // Bonus: paging, e.g. sort=appointmentDate,desc
    @GetMapping("/page")
    public Page<Appointment> page(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  @RequestParam(defaultValue = "appointmentDate,asc") String sort) {
        String[] parts = sort.split(",");
        Sort.Direction direction = parts.length > 1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, parts[0]));
        return appointmentService.getPage(pageable);
    }

    // Bonus: clean old cancellations
    @DeleteMapping("/cancelled-before")
    public String cancelledBefore(@RequestParam String date) {
        return appointmentService.deleteCancelledBefore(LocalDate.parse(date));
    }

    // ---- earlier endpoints ----
    @GetMapping("/pending/doctor/{doctorId}")
    public List<Appointment> pendingForDoctor(@PathVariable UUID doctorId) {
        return appointmentRepository.findByDoctorIdAndStatusOrderByAppointmentDateAsc(doctorId, AppointmentStatus.PENDING);
    }

    @GetMapping("/pending/doctor")
    public ResponseEntity<List<Appointment>> pendingForDoctorByName(@RequestParam String firstName,
                                                                    @RequestParam String lastName) {
        List<Doctor> doctors = doctorRepository.findByFirstNameAndLastName(firstName, lastName);
        if (doctors.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(appointmentRepository
                .findByDoctorIdAndStatusOrderByAppointmentDateAsc(doctors.get(0).getId(), AppointmentStatus.PENDING));
    }

    @GetMapping("/pending/past")
    public List<AppointmentDetailsDTO> pendingPast(@RequestParam(required = false) String beforeDate) {
        LocalDate cutoff = beforeDate == null ? LocalDate.now() : LocalDate.parse(beforeDate);
        return appointmentRepository.findPendingPastAppointmentsAsDto(AppointmentStatus.PENDING, cutoff);
    }

    @GetMapping("/count-by-doctor")
    public List<DoctorAppointmentCountDTO> countByDoctor() {
        return appointmentRepository.countAppointmentsPerDoctor();
    }

    @GetMapping("/specialization/{name}")
    public List<Appointment> bySpecialization(@PathVariable String name) {
        return appointmentRepository.findByDoctorSpecializationName(name);
    }

    @GetMapping("/doctor/{doctorId}")
    public Page<Appointment> byDoctor(@PathVariable UUID doctorId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateAsc(doctorId, PageRequest.of(page, size));
    }
}
