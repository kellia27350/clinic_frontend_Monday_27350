package kigali.clinc.clinicmanagement.domain;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "patient")
public class Patient {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name")  private String firstName;
    @Column(name = "last_name")   private String lastName;
    @Column(name = "date_of_birth") private Date dateOfBirth;

    @Column(name = "ss_number", unique = true)
    private String ssNumber;

    // hidden from JSON to avoid infinite loop
    @JsonIgnore
    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointments = new ArrayList<>();

    public UUID getId()                              { return id; }
    public String getFirstName()                     { return firstName; }
    public void setFirstName(String firstName)       { this.firstName = firstName; }
    public String getLastName()                      { return lastName; }
    public void setLastName(String lastName)         { this.lastName = lastName; }
    public Date getDateOfBirth()                     { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth)     { this.dateOfBirth = dateOfBirth; }
    public String getSsNumber()                      { return ssNumber; }
    public void setSsNumber(String ssNumber)         { this.ssNumber = ssNumber; }
    public List<Appointment> getAppointments()       { return appointments; }
    public void setAppointments(List<Appointment> a) { this.appointments = a; }
}
