package com.example.patient__service.service;

import com.example.patient__service.dto.PatientRequestDto;
import com.example.patient__service.dto.PatientResponseDto;
import com.example.patient__service.exception.EmailAlreadyExistsException;
import com.example.patient__service.exception.PatientNotFoundException;
import com.example.patient__service.mapper.PatientMapper;
import com.example.patient__service.model.Patient;
import com.example.patient__service.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDto> getPatients() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto) {
        if (patientRepository.existsByEmail(patientRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "A patient with this email " + "already exists"
                            + patientRequestDto.getEmail());
        }
        Patient newPatient = patientRepository.save(
                PatientMapper.toModel(patientRequestDto));

        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDto updatePatient(UUID id, PatientRequestDto patientRequestDto) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id" + id));
        if (patientRepository.existsByEmailAndIdNot(patientRequestDto.getEmail(), id)) {
            throw new EmailAlreadyExistsException(
                    "A patient with this email " + "already exists"
                            + patientRequestDto.getEmail());
        }
        patient.setName(patientRequestDto.getName());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));

        return PatientMapper.toDTO(patientRepository.save(patient));
    }
    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }

}
