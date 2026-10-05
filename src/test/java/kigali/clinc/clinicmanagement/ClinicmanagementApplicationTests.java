package kigali.clinc.clinicmanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.dto.AppointmentDetailsDTO;
import kigali.clinc.clinicmanagement.dto.DoctorAppointmentCountDTO;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@SpringBootTest
@Transactional
class ClinicmanagementApplicationTests {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private SpecializationRepository specializationRepository;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
        specializationRepository.deleteAll();
        officeRepository.deleteAll();

        Office officeA = new Office();
        officeA.setName("North Wing");
        officeA.setOfficeNumber(101);
        officeRepository.save(officeA);

        Office officeB = new Office();
        officeB.setName("South Wing");
        officeB.setOfficeNumber(202);
        officeRepository.save(officeB);

        Doctor doctorA = new Doctor();
        doctorA.setFirstName("Alice");
        doctorA.setLastName("Ngoma");
        doctorA.setDateOfBirth(Date.valueOf("1980-03-15"));
        doctorA.setOffice(officeA);
        doctorRepository.save(doctorA);

        Doctor doctorB = new Doctor();
        doctorB.setFirstName("Brian");
        doctorB.setLastName("Mugisha");
        doctorB.setDateOfBirth(Date.valueOf("1978-11-22"));
        doctorB.setOffice(officeB);
        doctorRepository.save(doctorB);

        Specialization cardiology = new Specialization();
        cardiology.setName("Cardiology");
        cardiology.setDoctors(List.of(doctorA));
        doctorA.getSpecializations().add(cardiology);
        specializationRepository.save(cardiology);

        Specialization neurology = new Specialization();
        neurology.setName("Neurology");
        neurology.setDoctors(List.of(doctorB));
        doctorB.getSpecializations().add(neurology);
        specializationRepository.save(neurology);

        Patient patientA = new Patient();
        patientA.setFirstName("Charlie");
        patientA.setLastName("Kamanzi");
        patientA.setDateOfBirth(Date.valueOf("1992-07-08"));
        patientRepository.save(patientA);

        Patient patientB = new Patient();
        patientB.setFirstName("Dana");
        patientB.setLastName("Niyibizi");
        patientB.setDateOfBirth(Date.valueOf("1975-12-04"));
        patientRepository.save(patientB);

        Patient patientC = new Patient();
        patientC.setFirstName("Eddy");
        patientC.setLastName("Uwimana");
        patientC.setDateOfBirth(Date.valueOf("2001-09-13"));
        patientRepository.save(patientC);

        Appointment pendingOne = new Appointment();
        pendingOne.setDoctor(doctorA);
        pendingOne.setPatient(patientA);
        pendingOne.setAppointmentDate(LocalDate.parse("2024-03-01"));
        pendingOne.setStatus(AppointmentStatus.PENDING);
        pendingOne.setReason("Annual checkup");

        Appointment pendingTwo = new Appointment();
        pendingTwo.setDoctor(doctorA);
        pendingTwo.setPatient(patientB);
        pendingTwo.setAppointmentDate(LocalDate.parse("2024-02-01"));
        pendingTwo.setStatus(AppointmentStatus.PENDING);
        pendingTwo.setReason("Blood pressure follow-up");

        Appointment pastPending = new Appointment();
        pastPending.setDoctor(doctorB);
        pastPending.setPatient(patientC);
        pastPending.setAppointmentDate(LocalDate.parse("2023-01-15"));
        pastPending.setStatus(AppointmentStatus.PENDING);
        pastPending.setReason("Old case");

        Appointment scheduled = new Appointment();
        scheduled.setDoctor(doctorB);
        scheduled.setPatient(patientA);
        scheduled.setAppointmentDate(LocalDate.parse("2024-04-15"));
        scheduled.setStatus(AppointmentStatus.SCHEDULED);
        scheduled.setReason("Consultation");

        appointmentRepository.saveAll(List.of(pendingOne, pendingTwo, pastPending, scheduled));
    }

    @Test
    void shouldFindPendingAppointmentsForDoctorOrderedByDateAsc() {
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndStatusOrderByAppointmentDateAsc(
            doctorRepository.findByFirstNameAndLastName("Alice", "Ngoma").get(0).getId(),
            AppointmentStatus.PENDING
        );

        assertEquals(2, appointments.size());
        assertEquals(LocalDate.parse("2024-02-01"), appointments.get(0).getAppointmentDate());
        assertEquals(LocalDate.parse("2024-03-01"), appointments.get(1).getAppointmentDate());
    }

    @Test
    void shouldReturnPendingPastAppointmentsAsDto() {
        List<AppointmentDetailsDTO> results = appointmentRepository.findPendingPastAppointmentsAsDto(
            AppointmentStatus.PENDING,
            LocalDate.parse("2024-01-01")
        );

        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(dto -> dto.patientName().equals("Eddy Uwimana")
            && dto.doctorName().equals("Brian Mugisha")
            && dto.date().equals(LocalDate.parse("2023-01-15"))));
        assertNotNull(results.get(0).date());
    }

    @Test
    void shouldCountAppointmentsPerDoctorBusiestFirst() {
        List<DoctorAppointmentCountDTO> results = appointmentRepository.countAppointmentsPerDoctor();

        assertEquals(2, results.size());
        assertEquals(2, results.get(0).appointmentCount());
    }

    @Test
    void shouldFindAppointmentsForDoctorsWithSpecificSpecialization() {
        List<Appointment> appointments = appointmentRepository.findByDoctorSpecializationName("Cardiology");

        assertEquals(2, appointments.size());
        assertTrue(appointments.stream().allMatch(a -> a.getDoctor().getFirstName().equals("Alice")));
    }

    @Test
    void shouldPaginateAppointmentsForDoctorOrderedByDate() {
        Doctor doctor = doctorRepository.findByFirstNameAndLastName("Alice", "Ngoma").get(0);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = appointmentRepository.findByDoctorIdOrderByAppointmentDateAsc(doctor.getId(), pageable);

        assertEquals(2, page.getTotalElements());
        assertEquals(LocalDate.parse("2024-02-01"), page.getContent().get(0).getAppointmentDate());
    }
}
