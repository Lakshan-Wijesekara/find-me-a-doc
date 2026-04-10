package com.findmeadoc.application.ports;

import com.findmeadoc.application.dto.DoctorProfileResponse;

public interface ViewDoctorProfileUseCase {
    DoctorProfileResponse  viewDoctorProfile(String email);
}
