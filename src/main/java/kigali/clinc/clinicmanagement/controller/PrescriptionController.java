package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.service.PrescriptionService;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired private PrescriptionService prescriptionService;

    // C1 — write a prescription (appointment must be COMPLETED)
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestParam UUID appointmentId,
                                       @RequestBody Prescription prescription) {
        String msg = prescriptionService.save(appointmentId, prescription);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.contains("only for"))       return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(msg, HttpStatus.CREATED);
    }

    // C2 — all prescriptions for one appointment
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> byAppointment(@PathVariable UUID appointmentId) {
        return prescriptionService.getByAppointment(appointmentId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>("The appointment with that id does not exist", HttpStatus.NOT_FOUND));
    }

    // C3 — full prescription history for a patient, newest first
    @GetMapping("/patient/{ssNumber}")
    public List<Prescription> byPatient(@PathVariable String ssNumber) {
        return prescriptionService.getByPatient(ssNumber);
    }

    // C4 — update medicine name, dosage, duration only
    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Prescription body) {
        String msg = prescriptionService.update(id, body);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    // C4 — delete a single prescription
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String msg = prescriptionService.delete(id);
        if (msg.contains("does not exist")) return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    // Bonus — medicines ranked by how many times they were prescribed
    @GetMapping("/stats/top-medicines")
    public List<Object[]> topMedicines() {
        return prescriptionService.topMedicines();
    }
}
