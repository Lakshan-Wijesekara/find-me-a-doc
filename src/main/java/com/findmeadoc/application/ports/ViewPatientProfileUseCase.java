package com.findmeadoc.application.ports;

import com.findmeadoc.application.dto.PatientProfileResponse;

public interface ViewPatientProfileUseCase {
    PatientProfileResponse viewPatientProfile(String email);
}
