package com.findmeadoc.application.ports;

import com.findmeadoc.application.dto.DoctorRegistrationRequest;
import com.findmeadoc.application.dto.DoctorRegistrationResponse;

// Returns the DoctorRegistrationResponse object and the type of request is DoctorRegistrationRequest
public interface DoctorRegistrationUseCase {
    DoctorRegistrationResponse registerDoctor(DoctorRegistrationRequest request);
}
