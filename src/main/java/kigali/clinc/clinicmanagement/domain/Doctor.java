package kigali.clinc.clinicmanagement.domain;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "doctor")
public class Doctor {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name")  private String firstName;
    @Column(name = "last_name")   private String lastName;
    @Column(name = "date_of_birth") private Date dateOfBirth;

    // doctor owns the office FK
    @OneToOne
    @JoinColumn(name = "office_id")
    private Office office;

    // doctor is the owner of the many-to-many link table
    @ManyToMany
    @JoinTable(
        name = "doctor_specialization",
        joinColumns = @JoinColumn(name = "doctor_id"),
        inverseJoinColumns = @JoinColumn(name = "specialization_id")
    )
    private List<Specialization> specializations = new ArrayList<>();

    // hidden from JSON to avoid infinite loop
    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments = new ArrayList<>();

    public UUID getId()                              { return id; }
    public String getFirstName()                     { return firstName; }
    public void setFirstName(String firstName)       { this.firstName = firstName; }
    public String getLastName()                      { return lastName; }
    public void setLastName(String lastName)         { this.lastName = lastName; }
    public Date getDateOfBirth()                     { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth)     { this.dateOfBirth = dateOfBirth; }
    public Office getOffice()                        { return office; }
    public void setOffice(Office office)             { this.office = office; }
    public List<Specialization> getSpecializations() { return specializations; }
    public void setSpecializations(List<Specialization> s) { this.specializations = s; }
    public List<Appointment> getAppointments()       { return appointments; }
    public void setAppointments(List<Appointment> a) { this.appointments = a; }
}
