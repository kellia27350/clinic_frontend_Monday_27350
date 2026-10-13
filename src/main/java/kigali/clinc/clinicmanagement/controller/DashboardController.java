package kigali.clinc.clinicmanagement.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import kigali.clinc.clinicmanagement.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private DashboardService dashboardService;

    // D4 — clinic summary: patients, doctors, freeOffices, appointmentsToday, scheduled, prescriptions
    @GetMapping("/summary")
    public Map<String, Long> summary() {
        return dashboardService.getSummary();
    }
}
