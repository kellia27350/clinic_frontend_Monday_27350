package kigali.clinc.clinicmanagement.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.service.DoctorService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired private DoctorService doctorService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Doctor doctor) {
        return new ResponseEntity<>(doctorService.saveDoctor(doctor), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<Doctor> all() { return doctorService.getAll(); }

    @GetMapping("/by-specialization")
    public List<Doctor> bySpecialization(@RequestParam String name) {
        return doctorService.getBySpecialization(name);
    }

    @GetMapping("/without-office")
    public List<Doctor> withoutOffice() { return doctorService.getWithoutOffice(); }

    // D3 — search doctors by first or last name, case-insensitive
    @GetMapping("/search")
    public List<Doctor> search(@RequestParam String name) {
        return doctorService.searchByName(name);
    }

    // A1 — add a specialization to a doctor
    @PutMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> addSpecialization(@PathVariable UUID doctorId,
                                                    @PathVariable UUID specializationId) {
        String msg = doctorService.addSpecialization(doctorId, specializationId);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.contains("already has"))    return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    // A2 — remove a specialization from a doctor (the specialization itself stays)
    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> removeSpecialization(@PathVariable UUID doctorId,
                                                       @PathVariable UUID specializationId) {
        String msg = doctorService.removeSpecialization(doctorId, specializationId);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.contains("does not have"))  return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    // A4 — move a doctor to a different office
    @PatchMapping("/{doctorId}/office")
    public ResponseEntity<String> moveOffice(@PathVariable UUID doctorId,
                                             @RequestParam int officeNumber) {
        String msg = doctorService.moveToOffice(doctorId, officeNumber);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.contains("taken"))          return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    // D1 — doctor's agenda for a day, CANCELLED appointments not included
    @GetMapping("/{doctorId}/agenda")
    public ResponseEntity<?> agenda(@PathVariable UUID doctorId, @RequestParam String date) {
        return doctorService.getAgenda(doctorId, LocalDate.parse(date))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND));
    }

    // Bonus — COMPLETED appointments per doctor between two dates
    @GetMapping("/workload")
    public List<Object[]> workload(@RequestParam String start, @RequestParam String end) {
        return doctorService.getWorkload(LocalDate.parse(start), LocalDate.parse(end));
    }
}
