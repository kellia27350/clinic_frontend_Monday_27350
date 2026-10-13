package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.service.AppointmentService;
import kigali.clinc.clinicmanagement.service.PatientService;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired private PatientService     patientService;
    @Autowired private AppointmentService appointmentService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Patient patient) {
        return new ResponseEntity<>(patientService.savePatient(patient), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<Patient> all() { return patientService.getAll(); }

    @GetMapping("/by-last-name")
    public List<Patient> byLastName(@RequestParam String lastName) {
        return patientService.getByLastName(lastName);
    }

    @GetMapping("/of-doctor/{doctorId}")
    public ResponseEntity<?> ofDoctor(@PathVariable UUID doctorId) {
        return patientService.getOfDoctor(doctorId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND));
    }

    @GetMapping("/frequent")
    public List<Patient> frequent(@RequestParam long min) {
        return patientService.getFrequent(min);
    }

    // D2 — upcoming SCHEDULED/CONFIRMED appointments for a patient, soonest first
    @GetMapping("/{ssNumber}/upcoming")
    public ResponseEntity<?> upcoming(@PathVariable String ssNumber) {
        return appointmentService.getUpcoming(ssNumber)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>("That patient with that ssnumber does not exist", HttpStatus.NOT_FOUND));
    }
}
