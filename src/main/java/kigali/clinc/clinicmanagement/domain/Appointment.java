package kigali.clinc.clinicmanagement.domain;

import java.time.LocalDate;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "appointment_date") private LocalDate appointmentDate;
    private String reason;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @ManyToOne @JoinColumn(name = "patient_id") private Patient patient;
    @ManyToOne @JoinColumn(name = "doctor_id")  private Doctor doctor;

    public UUID getId()                              { return id; }
    public LocalDate getAppointmentDate()            { return appointmentDate; }
    public void setAppointmentDate(LocalDate date)   { this.appointmentDate = date; }
    public String getReason()                        { return reason; }
    public void setReason(String reason)             { this.reason = reason; }
    public AppointmentStatus getStatus()             { return status; }
    public void setStatus(AppointmentStatus status)  { this.status = status; }
    public Patient getPatient()                      { return patient; }
    public void setPatient(Patient patient)          { this.patient = patient; }
    public Doctor getDoctor()                        { return doctor; }
    public void setDoctor(Doctor doctor)             { this.doctor = doctor; }
}
