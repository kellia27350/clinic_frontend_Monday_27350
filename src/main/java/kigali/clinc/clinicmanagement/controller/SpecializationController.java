package kigali.clinc.clinicmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.service.SpecializationService;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    @Autowired
    private SpecializationService specService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Specialization spec) {
        String message = specService.saveSpecialization(spec);
        HttpStatus status = message.endsWith("Successfully") ? HttpStatus.CREATED : HttpStatus.CONFLICT;
        return new ResponseEntity<>(message, status);
    }

    @GetMapping("/all")
    public List<Specialization> all() {
        return specService.getAll();
    }

    // B3
    @GetMapping("/unused")
    public List<Specialization> unused() {
        return specService.getUnused();
    }
}
