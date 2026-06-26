package com.appttracker.controller;

import com.appttracker.model.StaffMember;
import com.appttracker.service.StaffMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class StaffMemberController {

    private final StaffMemberService staffMemberService;

    // GET /api/staff — returns all staff members
    @GetMapping
    public ResponseEntity<List<StaffMember>> getAllStaffMembers() {
        return ResponseEntity.ok(staffMemberService.getAllStaffMembers());
    }

    // GET /api/staff/{id} — returns a single staff member by ID
    @GetMapping("/{id}")
    public ResponseEntity<StaffMember> getStaffMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(staffMemberService.getStaffMemberById(id));
    }

    // POST /api/staff — creates a new staff member
    @PostMapping
    public ResponseEntity<StaffMember> createStaffMember(@RequestBody StaffMember staffMember) {
        StaffMember saved = staffMemberService.createStaffMember(staffMember);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
