package com.example.patient__service.mapper;

import com.example.patient__service.dto.PatientRequestDto;
import com.example.patient__service.dto.PatientResponseDto;
import com.example.patient__service.model.Patient;

import java.time.LocalDate;

public class PatientMapper {
    public static PatientResponseDto toDTO(Patient patient){
        PatientResponseDto patientResponseDto = new PatientResponseDto();

        patientResponseDto.setId(patient.getId().toString());
        patientResponseDto.setName(patient.getName());
        patientResponseDto.setAddress(patient.getAddress());
        patientResponseDto.setEmail(patient.getEmail());
        patientResponseDto.setDateOfBirth(patient.getDateOfBirth().toString());

        return patientResponseDto;

    }
    public static Patient toModel(PatientRequestDto patientRequestDto){
        Patient patient = new Patient();

        patient.setName(patientRequestDto.getName());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDto.getRegisteredDate()));

        return patient;
    }
}
