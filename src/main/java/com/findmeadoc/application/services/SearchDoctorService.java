package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.DoctorProfileResponse;
import com.findmeadoc.application.ports.SearchDoctorUseCase;
import com.findmeadoc.domain.models.Doctor;
import com.findmeadoc.domain.repositories.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchDoctorService implements SearchDoctorUseCase {
    private final DoctorRepository doctorRepository;

    public SearchDoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public List<DoctorProfileResponse> searchDoctors(String specialty) {

        List<Doctor> doctors;

        if (specialty == null || specialty.isEmpty()) {
            // If no specialty is provided, return all doctors
            doctors = doctorRepository.findAll();
        } else {
            doctors = doctorRepository.findBySpecialization(specialty);
        }

        return doctors.stream()
                .map(doctor -> new DoctorProfileResponse(
                        doctor.getId(),
                        doctor.getUser().getFullName(),
                        doctor.getSpecialization(),
                        doctor.getConsultationFee(),
                        doctor.getUser().getEmail(),
                        doctor.getUser().getPhoneNumber()
                ))
                .toList();
    }

}
