package com.appttracker.repository;

import com.appttracker.model.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffMemberRepository extends JpaRepository<StaffMember, Long> {
    // JpaRepository provides: save, findById, findAll, deleteById, count, etc.
    // No additional methods needed for basic CRUD
}
