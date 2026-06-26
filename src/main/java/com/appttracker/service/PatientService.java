package com.appttracker.service;

import com.appttracker.exception.ResourceNotFoundException;
import com.appttracker.model.Patient;
import com.appttracker.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }
}
