package kigali.clinc.clinicmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.service.OfficeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController 
@RequestMapping({"/api/offices", "/api/office"})
public class OfficeController {
    

    @Autowired 
    private OfficeService officeServes;


    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces  = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?>  saveOffice(@RequestBody  Office office){

        String returnedMessage = officeServes.saveOffice(office);

        if(returnedMessage.equalsIgnoreCase("Office is saved Successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
        
    }

    @GetMapping("/all")
    public List<Office> all() {
        return officeServes.getAll();
    }

   
    @GetMapping("/busiest")
    public ResponseEntity<?> busiest() {
        List<Object[]> top = officeServes.getBusiest();
        if (top.isEmpty()) {
            return ResponseEntity.ok("No appointments yet");
        }
        return ResponseEntity.ok(top.get(0));
    }
}
