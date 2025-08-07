package com.example.patient__service.dto;

import billing.BillingResponse;

public class PatientWithBillingResponseDto {


    private PatientResponseDto patient;
    private BillingResponseDto billing;

    public PatientWithBillingResponseDto(PatientResponseDto patient, BillingResponseDto billing) {
        this.patient = patient;
        this.billing = billing;
    }

    public PatientResponseDto getPatient() {
        return patient;
    }

    public BillingResponseDto getBilling() {
        return billing;
    }
    }

