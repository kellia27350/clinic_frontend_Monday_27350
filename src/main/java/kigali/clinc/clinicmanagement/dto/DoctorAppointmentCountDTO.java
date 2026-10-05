package kigali.clinc.clinicmanagement.dto;

import java.util.UUID;

public record DoctorAppointmentCountDTO(UUID doctorId, Long appointmentCount) {
}
