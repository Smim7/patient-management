package com.example.patient__service.service;

import billing.BillingResponse;
import com.example.patient__service.dto.BillingResponseDto;
import com.example.patient__service.dto.PatientRequestDto;
import com.example.patient__service.dto.PatientResponseDto;
import com.example.patient__service.dto.PatientWithBillingResponseDto;
import com.example.patient__service.exception.EmailAlreadyExistsException;
import com.example.patient__service.exception.PatientNotFoundException;
import com.example.patient__service.grpc.BillingServiceGrpcClient;
import com.example.patient__service.kafka.KafkaProducer;
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
    private  final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
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

        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),
                newPatient.getName(),newPatient.getEmail());

        kafkaProducer.sendEvent(newPatient);


        return PatientMapper.toDTO(newPatient);
    }
//public PatientWithBillingResponseDto createPatient(PatientRequestDto patientRequestDto) {
//    if (patientRepository.existsByEmail(patientRequestDto.getEmail())) {
//        throw new EmailAlreadyExistsException(
//                "A patient with this email already exists: " + patientRequestDto.getEmail());
//    }
//
//    Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDto));
//
//    BillingResponse grpcResponse = billingServiceGrpcClient.createBillingAccount(
//            newPatient.getId().toString(),
//            newPatient.getName(),
//            newPatient.getEmail());
//
//    BillingResponseDto billingDto = new BillingResponseDto(
//            grpcResponse.getAccountId(),     // example field
//            grpcResponse.getStatus()         // example field
//    );
//
//    return new PatientWithBillingResponseDto(PatientMapper.toDTO(newPatient), billingDto);
//}

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
