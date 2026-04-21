package com.findmeadoc.application.ports;

import com.findmeadoc.application.dto.PatientRegistrationRequest;
import com.findmeadoc.application.dto.PatientRegistrationResponse;

// Returns the DoctorRegistrationResponse object and the type of request is DoctorRegistrationRequest
public interface PatientRegistrationUseCase {
    PatientRegistrationResponse registerPatient(PatientRegistrationRequest request);
}
