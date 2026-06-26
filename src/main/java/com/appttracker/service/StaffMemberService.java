package com.appttracker.service;

import com.appttracker.exception.ResourceNotFoundException;
import com.appttracker.model.StaffMember;
import com.appttracker.repository.StaffMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffMemberService {

    private final StaffMemberRepository staffMemberRepository;

    public List<StaffMember> getAllStaffMembers() {
        return staffMemberRepository.findAll();
    }

    public StaffMember getStaffMemberById(Long id) {
        return staffMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StaffMember", id));
    }

    public StaffMember createStaffMember(StaffMember staffMember) {
        return staffMemberRepository.save(staffMember);
    }
}
