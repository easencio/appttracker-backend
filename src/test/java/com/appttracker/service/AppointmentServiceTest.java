package com.appttracker.service;

import com.appttracker.exception.BookingConflictException;
import com.appttracker.exception.ResourceNotFoundException;
import com.appttracker.model.Appointment;
import com.appttracker.model.Patient;
import com.appttracker.model.StaffMember;
import com.appttracker.repository.AppointmentRepository;
import com.appttracker.repository.PatientRepository;
import com.appttracker.repository.StaffMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private StaffMemberRepository staffMemberRepository;
    @Mock private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private StaffMember staffMember;
    private Patient patient;
    private Appointment appointment;

    private static final LocalDate DATE       = LocalDate.of(2025, 6, 15);
    private static final LocalTime START_TIME = LocalTime.of(9, 0);
    private static final LocalTime END_TIME   = LocalTime.of(10, 0);

    @BeforeEach
    void setUp() {
        staffMember = StaffMember.builder()
                .id(1L).firstName("Alice").lastName("Brown").gender("Female").credentials("MD")
                .build();

        patient = Patient.builder()
                .id(1L).firstName("Jane").lastName("Doe").gender("Female").age(30)
                .build();

        appointment = Appointment.builder()
                .staffMember(StaffMember.builder().id(1L).build())
                .patients(List.of(Patient.builder().id(1L).build()))
                .appointmentDate(DATE)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .build();
    }

    // --- getAllAppointments ---

    @Test
    void getAllAppointments_returnsAllAppointments() {
        Appointment existing = Appointment.builder().id(1L).build();
        when(appointmentRepository.findAll()).thenReturn(List.of(existing));

        List<Appointment> result = appointmentService.getAllAppointments();

        assertThat(result).hasSize(1);
        verify(appointmentRepository).findAll();
    }

    // --- getAppointmentById ---

    @Test
    void getAppointmentById_returnsAppointmentWhenFound() {
        Appointment existing = Appointment.builder().id(1L).build();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existing));

        Appointment result = appointmentService.getAppointmentById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getAppointmentById_throwsResourceNotFoundExceptionWhenNotFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.getAppointmentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appointment")
                .hasMessageContaining("99");
    }

    // --- createAppointment: happy path ---

    @Test
    void createAppointment_savesAppointmentWhenNoConflicts() {
        when(staffMemberRepository.findById(1L)).thenReturn(Optional.of(staffMember));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findConflictingAppointmentsForStaff(
                any(), any(), any(), any())).thenReturn(List.of());
        when(appointmentRepository.findConflictingAppointmentsForPatient(
                any(), any(), any(), any())).thenReturn(List.of());

        Appointment saved = Appointment.builder().id(10L).build();
        when(appointmentRepository.save(any())).thenReturn(saved);

        Appointment result = appointmentService.createAppointment(appointment);

        assertThat(result.getId()).isEqualTo(10L);
        verify(appointmentRepository).save(any());
    }

    // --- createAppointment: staff member not found ---

    @Test
    void createAppointment_throwsResourceNotFoundExceptionWhenStaffMemberNotFound() {
        when(staffMemberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.createAppointment(appointment))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("StaffMember");

        verify(appointmentRepository, never()).save(any());
    }

    // --- createAppointment: patient not found ---

    @Test
    void createAppointment_throwsResourceNotFoundExceptionWhenPatientNotFound() {
        when(staffMemberRepository.findById(1L)).thenReturn(Optional.of(staffMember));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.createAppointment(appointment))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient");

        verify(appointmentRepository, never()).save(any());
    }

    // --- createAppointment: staff double-booking ---

    @Test
    void createAppointment_throwsBookingConflictExceptionWhenStaffAlreadyBooked() {
        when(staffMemberRepository.findById(1L)).thenReturn(Optional.of(staffMember));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Simulate an existing conflicting appointment for the staff member
        Appointment conflict = Appointment.builder().id(99L).build();
        when(appointmentRepository.findConflictingAppointmentsForStaff(
                eq(staffMember), eq(DATE), eq(START_TIME), eq(END_TIME)))
                .thenReturn(List.of(conflict));

        assertThatThrownBy(() -> appointmentService.createAppointment(appointment))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("Alice Brown");

        verify(appointmentRepository, never()).save(any());
    }

    // --- createAppointment: patient double-booking ---

    @Test
    void createAppointment_throwsBookingConflictExceptionWhenPatientAlreadyBooked() {
        when(staffMemberRepository.findById(1L)).thenReturn(Optional.of(staffMember));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // No staff conflict, but patient is already booked
        when(appointmentRepository.findConflictingAppointmentsForStaff(
                any(), any(), any(), any())).thenReturn(List.of());

        Appointment conflict = Appointment.builder().id(99L).build();
        when(appointmentRepository.findConflictingAppointmentsForPatient(
                eq(patient), eq(DATE), eq(START_TIME), eq(END_TIME)))
                .thenReturn(List.of(conflict));

        assertThatThrownBy(() -> appointmentService.createAppointment(appointment))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("Jane Doe");

        verify(appointmentRepository, never()).save(any());
    }

    // --- createAppointment: multiple patients, one is double-booked ---

    @Test
    void createAppointment_throwsBookingConflictExceptionForSecondPatientWhenDoubleBooked() {
        Patient patient2 = Patient.builder()
                .id(2L).firstName("Bob").lastName("Smith").gender("Male").age(25)
                .build();

        // Appointment with two patients
        Appointment twoPatientAppt = Appointment.builder()
                .staffMember(StaffMember.builder().id(1L).build())
                .patients(List.of(
                        Patient.builder().id(1L).build(),
                        Patient.builder().id(2L).build()))
                .appointmentDate(DATE)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .build();

        when(staffMemberRepository.findById(1L)).thenReturn(Optional.of(staffMember));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient2));
        when(appointmentRepository.findConflictingAppointmentsForStaff(
                any(), any(), any(), any())).thenReturn(List.of());
        when(appointmentRepository.findConflictingAppointmentsForPatient(
                eq(patient), any(), any(), any())).thenReturn(List.of());

        // Second patient is double-booked
        Appointment conflict = Appointment.builder().id(99L).build();
        when(appointmentRepository.findConflictingAppointmentsForPatient(
                eq(patient2), eq(DATE), eq(START_TIME), eq(END_TIME)))
                .thenReturn(List.of(conflict));

        assertThatThrownBy(() -> appointmentService.createAppointment(twoPatientAppt))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("Bob Smith");

        verify(appointmentRepository, never()).save(any());
    }
}
