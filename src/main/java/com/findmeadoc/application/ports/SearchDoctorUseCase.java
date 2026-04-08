package com.findmeadoc.application.ports;

import com.findmeadoc.application.dto.DoctorProfileResponse;

import java.util.List;

public interface SearchDoctorUseCase {
    List<DoctorProfileResponse> searchDoctors(String specialty);
}
