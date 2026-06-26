package com.appttracker.repository;

import com.appttracker.model.Appointment;
import com.appttracker.model.Patient;
import com.appttracker.model.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Used for double-booking validation: find appointments for a staff member
    // that overlap with the requested time slot on the same date
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.staffMember = :staffMember
          AND a.appointmentDate = :date
          AND a.startTime < :endTime
          AND a.endTime > :startTime
    """)
    List<Appointment> findConflictingAppointmentsForStaff(
        @Param("staffMember") StaffMember staffMember,
        @Param("date") LocalDate date,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    // Used for double-booking validation: find appointments for a patient
    // that overlap with the requested time slot on the same date
    @Query("""
        SELECT a FROM Appointment a
        JOIN a.patients p
        WHERE p = :patient
          AND a.appointmentDate = :date
          AND a.startTime < :endTime
          AND a.endTime > :startTime
    """)
    List<Appointment> findConflictingAppointmentsForPatient(
        @Param("patient") Patient patient,
        @Param("date") LocalDate date,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );
}
