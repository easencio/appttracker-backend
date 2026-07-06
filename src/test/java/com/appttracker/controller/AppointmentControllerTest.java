package com.appttracker.controller;

import com.appttracker.exception.BookingConflictException;
import com.appttracker.exception.ResourceNotFoundException;
import com.appttracker.model.Appointment;
import com.appttracker.model.Patient;
import com.appttracker.model.StaffMember;
import com.appttracker.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    private ObjectMapper objectMapper;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        // Register JavaTimeModule so ObjectMapper handles LocalDate/LocalTime
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        StaffMember staff = StaffMember.builder()
                .id(1L).firstName("Alice").lastName("Brown").gender("Female").credentials("MD")
                .build();
        Patient patient = Patient.builder()
                .id(1L).firstName("Jane").lastName("Doe").gender("Female").age(30)
                .build();

        appointment = Appointment.builder()
                .id(1L)
                .staffMember(staff)
                .patients(List.of(patient))
                .appointmentDate(LocalDate.of(2025, 6, 15))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .build();
    }

    // --- GET /api/appointments ---

    @Test
    void getAllAppointments_returns200WithAppointmentList() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(List.of(appointment));

        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllAppointments_returns200WithEmptyList() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(List.of());

        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/appointments/{id} ---

    @Test
    void getAppointmentById_returns200WhenFound() throws Exception {
        when(appointmentService.getAppointmentById(1L)).thenReturn(appointment);

        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.staffMember.firstName").value("Alice"));
    }

    @Test
    void getAppointmentById_returns404WhenNotFound() throws Exception {
        when(appointmentService.getAppointmentById(99L))
                .thenThrow(new ResourceNotFoundException("Appointment", 99L));

        mockMvc.perform(get("/api/appointments/99"))
                .andExpect(status().isNotFound());
    }

    // --- POST /api/appointments ---

    @Test
    void createAppointment_returns201WhenValid() throws Exception {
        when(appointmentService.createAppointment(any())).thenReturn(appointment);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.staffMember.lastName").value("Brown"))
                .andExpect(jsonPath("$.patients.length()").value(1));
    }

    @Test
    void createAppointment_returns409WhenBookingConflict() throws Exception {
        when(appointmentService.createAppointment(any()))
                .thenThrow(new BookingConflictException("Staff member Alice Brown already has an appointment."));

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointment)))
                .andExpect(status().isConflict());
    }

    @Test
    void createAppointment_returns404WhenStaffOrPatientNotFound() throws Exception {
        when(appointmentService.createAppointment(any()))
                .thenThrow(new ResourceNotFoundException("StaffMember", 99L));

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointment)))
                .andExpect(status().isNotFound());
    }
}
