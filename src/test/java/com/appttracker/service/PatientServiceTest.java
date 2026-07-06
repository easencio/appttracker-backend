package com.appttracker.service;

import com.appttracker.exception.ResourceNotFoundException;
import com.appttracker.model.Patient;
import com.appttracker.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .gender("Female")
                .age(30)
                .build();
    }

    // --- getAllPatients ---

    @Test
    void getAllPatients_returnsAllPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<Patient> result = patientService.getAllPatients();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Jane");
        verify(patientRepository).findAll();
    }

    @Test
    void getAllPatients_returnsEmptyListWhenNoPatients() {
        when(patientRepository.findAll()).thenReturn(List.of());

        List<Patient> result = patientService.getAllPatients();

        assertThat(result).isEmpty();
    }

    // --- getPatientById ---

    @Test
    void getPatientById_returnsPatientWhenFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLastName()).isEqualTo("Doe");
    }

    @Test
    void getPatientById_throwsResourceNotFoundExceptionWhenNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient")
                .hasMessageContaining("99");
    }

    // --- createPatient ---

    @Test
    void createPatient_savesAndReturnsPatient() {
        Patient input = Patient.builder()
                .firstName("John")
                .lastName("Smith")
                .gender("Male")
                .age(45)
                .build();
        Patient saved = Patient.builder()
                .id(2L)
                .firstName("John")
                .lastName("Smith")
                .gender("Male")
                .age(45)
                .build();

        when(patientRepository.save(input)).thenReturn(saved);

        Patient result = patientService.createPatient(input);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getFirstName()).isEqualTo("John");
        verify(patientRepository).save(input);
    }
}
