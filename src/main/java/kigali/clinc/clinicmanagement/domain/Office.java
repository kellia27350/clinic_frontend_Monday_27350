package kigali.clinc.clinicmanagement.domain;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "office")
public class Office {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(name = "office_number", unique = true)
    private int officeNumber;

    // hidden from JSON — we don't want the doctor nested inside the office
    @JsonIgnore
    @OneToOne(mappedBy = "office")
    private Doctor doctor;

    public UUID getId()                  { return id; }
    public String getName()              { return name; }
    public void setName(String name)     { this.name = name; }
    public int getOfficeNumber()         { return officeNumber; }
    public void setOfficeNumber(int n)   { this.officeNumber = n; }
    public Doctor getDoctor()            { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
}
