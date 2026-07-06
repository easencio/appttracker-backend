package com.appttracker.controller;

import com.appttracker.exception.ResourceNotFoundException;
import com.appttracker.model.Patient;
import com.appttracker.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientService patientService;

    // --- GET /api/patients ---

    @Test
    void getAllPatients_returns200WithPatientList() throws Exception {
        Patient p = Patient.builder()
                .id(1L).firstName("Jane").lastName("Doe").gender("Female").age(30)
                .build();
        when(patientService.getAllPatients()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Jane"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    void getAllPatients_returns200WithEmptyList() throws Exception {
        when(patientService.getAllPatients()).thenReturn(List.of());

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/patients/{id} ---

    @Test
    void getPatientById_returns200WhenFound() throws Exception {
        Patient p = Patient.builder()
                .id(1L).firstName("Jane").lastName("Doe").gender("Female").age(30)
                .build();
        when(patientService.getPatientById(1L)).thenReturn(p);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void getPatientById_returns404WhenNotFound() throws Exception {
        when(patientService.getPatientById(99L))
                .thenThrow(new ResourceNotFoundException("Patient", 99L));

        mockMvc.perform(get("/api/patients/99"))
                .andExpect(status().isNotFound());
    }

    // --- POST /api/patients ---

    @Test
    void createPatient_returns201WithSavedPatient() throws Exception {
        Patient input = Patient.builder()
                .firstName("John").lastName("Smith").gender("Male").age(45)
                .build();
        Patient saved = Patient.builder()
                .id(2L).firstName("John").lastName("Smith").gender("Male").age(45)
                .build();
        when(patientService.createPatient(any())).thenReturn(saved);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstName").value("John"));
    }
}
