package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.service.SpecializationService;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    @Autowired private SpecializationService specService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Specialization spec) {
        String msg = specService.saveSpecialization(spec);
        HttpStatus status = msg.contains("Successfully") ? HttpStatus.CREATED : HttpStatus.CONFLICT;
        return new ResponseEntity<>(msg, status);
    }

    @GetMapping("/all")
    public List<Specialization> all() { return specService.getAll(); }

    // specializations not assigned to any doctor yet
    @GetMapping("/unused")
    public List<Specialization> unused() { return specService.getUnused(); }
}
