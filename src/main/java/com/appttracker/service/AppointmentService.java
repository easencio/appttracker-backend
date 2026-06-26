package com.appttracker.service;

import com.appttracker.exception.BookingConflictException;
import com.appttracker.exception.ResourceNotFoundException;
import com.appttracker.model.Appointment;
import com.appttracker.model.Patient;
import com.appttracker.model.StaffMember;
import com.appttracker.repository.AppointmentRepository;
import com.appttracker.repository.PatientRepository;
import com.appttracker.repository.StaffMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final PatientRepository patientRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    public Appointment createAppointment(Appointment appointment) {
        // Resolve staff member from DB to ensure it exists
        StaffMember staffMember = staffMemberRepository
                .findById(appointment.getStaffMember().getId())
                .orElseThrow(() -> new ResourceNotFoundException("StaffMember",
                        appointment.getStaffMember().getId()));

        // Resolve each patient from DB to ensure they exist
        List<Patient> patients = appointment.getPatients().stream()
                .map(p -> patientRepository.findById(p.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Patient", p.getId())))
                .toList();

        // Check staff member is not double-booked
        List<Appointment> staffConflicts = appointmentRepository
                .findConflictingAppointmentsForStaff(
                        staffMember,
                        appointment.getAppointmentDate(),
                        appointment.getStartTime(),
                        appointment.getEndTime());

        if (!staffConflicts.isEmpty()) {
            throw new BookingConflictException(
                    "Staff member " + staffMember.getFirstName() + " " + staffMember.getLastName()
                    + " already has an appointment during the requested time slot.");
        }

        // Check each patient is not double-booked
        for (Patient patient : patients) {
            List<Appointment> patientConflicts = appointmentRepository
                    .findConflictingAppointmentsForPatient(
                            patient,
                            appointment.getAppointmentDate(),
                            appointment.getStartTime(),
                            appointment.getEndTime());

            if (!patientConflicts.isEmpty()) {
                throw new BookingConflictException(
                        "Patient " + patient.getFirstName() + " " + patient.getLastName()
                        + " already has an appointment during the requested time slot.");
            }
        }

        // All validations passed — persist the appointment
        appointment.setStaffMember(staffMember);
        appointment.setPatients(patients);
        return appointmentRepository.save(appointment);
    }
}
