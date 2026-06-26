package com.appttracker.controller;

import com.appttracker.model.Patient;
import com.appttracker.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    // GET /api/patients — returns all patients
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // GET /api/patients/{id} — returns a single patient by ID
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    // POST /api/patients — creates a new patient
    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient saved = patientService.createPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
