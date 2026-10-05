package kigali.clinc.clinicmanagement.dto;

import java.time.LocalDate;

public record AppointmentDetailsDTO(String patientName, String doctorName, LocalDate date) {
}
