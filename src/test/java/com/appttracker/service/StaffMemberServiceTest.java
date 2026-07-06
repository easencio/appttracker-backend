package com.appttracker.service;

import com.appttracker.exception.ResourceNotFoundException;
import com.appttracker.model.StaffMember;
import com.appttracker.repository.StaffMemberRepository;
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
class StaffMemberServiceTest {

    @Mock
    private StaffMemberRepository staffMemberRepository;

    @InjectMocks
    private StaffMemberService staffMemberService;

    private StaffMember staffMember;

    @BeforeEach
    void setUp() {
        staffMember = StaffMember.builder()
                .id(1L)
                .firstName("Dr. Alice")
                .lastName("Brown")
                .gender("Female")
                .credentials("MD")
                .build();
    }

    // --- getAllStaffMembers ---

    @Test
    void getAllStaffMembers_returnsAllStaffMembers() {
        when(staffMemberRepository.findAll()).thenReturn(List.of(staffMember));

        List<StaffMember> result = staffMemberService.getAllStaffMembers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCredentials()).isEqualTo("MD");
        verify(staffMemberRepository).findAll();
    }

    @Test
    void getAllStaffMembers_returnsEmptyListWhenNoStaffMembers() {
        when(staffMemberRepository.findAll()).thenReturn(List.of());

        List<StaffMember> result = staffMemberService.getAllStaffMembers();

        assertThat(result).isEmpty();
    }

    // --- getStaffMemberById ---

    @Test
    void getStaffMemberById_returnsStaffMemberWhenFound() {
        when(staffMemberRepository.findById(1L)).thenReturn(Optional.of(staffMember));

        StaffMember result = staffMemberService.getStaffMemberById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Dr. Alice");
    }

    @Test
    void getStaffMemberById_throwsResourceNotFoundExceptionWhenNotFound() {
        when(staffMemberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> staffMemberService.getStaffMemberById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("StaffMember")
                .hasMessageContaining("99");
    }

    // --- createStaffMember ---

    @Test
    void createStaffMember_savesAndReturnsStaffMember() {
        StaffMember input = StaffMember.builder()
                .firstName("Bob")
                .lastName("Jones")
                .gender("Male")
                .credentials("RN")
                .build();
        StaffMember saved = StaffMember.builder()
                .id(2L)
                .firstName("Bob")
                .lastName("Jones")
                .gender("Male")
                .credentials("RN")
                .build();

        when(staffMemberRepository.save(input)).thenReturn(saved);

        StaffMember result = staffMemberService.createStaffMember(input);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getCredentials()).isEqualTo("RN");
        verify(staffMemberRepository).save(input);
    }
}
