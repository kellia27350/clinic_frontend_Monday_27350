package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.service.OfficeService;

@RestController
@RequestMapping({"/api/offices", "/api/office"})
public class OfficeController {

    @Autowired private OfficeService officeService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Office office) {
        String msg = officeService.save(office);
        HttpStatus status = msg.contains("Successfully") ? HttpStatus.CREATED : HttpStatus.CONFLICT;
        return new ResponseEntity<>(msg, status);
    }

    @GetMapping("/all")
    public List<Office> all() { return officeService.getAll(); }

    // A3 — offices with no doctor
    @GetMapping("/free")
    public List<Office> free() { return officeService.getFreeOffices(); }

    @GetMapping("/busiest")
    public ResponseEntity<?> busiest() {
        List<Object[]> top = officeService.getBusiest();
        return top.isEmpty() ? ResponseEntity.ok("No appointments yet") : ResponseEntity.ok(top.get(0));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable java.util.UUID id) {
        String msg = officeService.delete(id);
        HttpStatus status = msg.contains("Successfully") ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(msg, status);
    }
}
