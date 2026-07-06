package com.appttracker.controller;

import com.appttracker.exception.ResourceNotFoundException;
import com.appttracker.model.StaffMember;
import com.appttracker.service.StaffMemberService;
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

@WebMvcTest(StaffMemberController.class)
class StaffMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StaffMemberService staffMemberService;

    // --- GET /api/staff ---

    @Test
    void getAllStaffMembers_returns200WithStaffList() throws Exception {
        StaffMember s = StaffMember.builder()
                .id(1L).firstName("Alice").lastName("Brown").gender("Female").credentials("MD")
                .build();
        when(staffMemberService.getAllStaffMembers()).thenReturn(List.of(s));

        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[0].credentials").value("MD"));
    }

    @Test
    void getAllStaffMembers_returns200WithEmptyList() throws Exception {
        when(staffMemberService.getAllStaffMembers()).thenReturn(List.of());

        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/staff/{id} ---

    @Test
    void getStaffMemberById_returns200WhenFound() throws Exception {
        StaffMember s = StaffMember.builder()
                .id(1L).firstName("Alice").lastName("Brown").gender("Female").credentials("MD")
                .build();
        when(staffMemberService.getStaffMemberById(1L)).thenReturn(s);

        mockMvc.perform(get("/api/staff/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.credentials").value("MD"));
    }

    @Test
    void getStaffMemberById_returns404WhenNotFound() throws Exception {
        when(staffMemberService.getStaffMemberById(99L))
                .thenThrow(new ResourceNotFoundException("StaffMember", 99L));

        mockMvc.perform(get("/api/staff/99"))
                .andExpect(status().isNotFound());
    }

    // --- POST /api/staff ---

    @Test
    void createStaffMember_returns201WithSavedStaffMember() throws Exception {
        StaffMember input = StaffMember.builder()
                .firstName("Bob").lastName("Jones").gender("Male").credentials("RN")
                .build();
        StaffMember saved = StaffMember.builder()
                .id(2L).firstName("Bob").lastName("Jones").gender("Male").credentials("RN")
                .build();
        when(staffMemberService.createStaffMember(any())).thenReturn(saved);

        mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.credentials").value("RN"));
    }
}
