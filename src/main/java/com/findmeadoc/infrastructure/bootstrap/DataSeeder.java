package com.findmeadoc.infrastructure.bootstrap;

import com.findmeadoc.domain.models.Doctor;
import com.findmeadoc.domain.models.Role;
import com.findmeadoc.domain.models.User;
import com.findmeadoc.domain.repositories.DoctorRepository;
import com.findmeadoc.domain.repositories.UserRepository; // Assuming you have this
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DataSeeder(DoctorRepository doctorRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (doctorRepository.count() == 0) {

            // --- Doctor 1 ---
            User user1 = new User();
            user1.setFullName("Gregory House");
            user1.setEmail("house@diagnostics.com");
            user1.setPhoneNumber("555-0101");
            // ADD THESE TWO LINES:
            user1.setPassword("dummy_password_hash");
            user1.setRole(Role.valueOf("DOCTOR"));

            userRepository.save(user1);

            Doctor doctor1 = new Doctor();
            doctor1.setUser(user1);
            doctor1.setSpecialization("Cardiology");
            doctor1.setConsultationFee(250.0);
            doctorRepository.save(doctor1);

            // --- Doctor 2 ---
            User user2 = new User();
            user2.setFullName("Meredith Grey");
            user2.setEmail("grey@hospital.com");
            user2.setPhoneNumber("555-0202");
            // ADD THESE TWO LINES:
            user2.setPassword("dummy_password_hash");
            user2.setRole(Role.valueOf("DOCTOR"));

            userRepository.save(user2);

            Doctor doctor2 = new Doctor();
            doctor2.setUser(user2);
            doctor2.setSpecialization("General Medicine");
            doctor2.setConsultationFee(150.0);
            doctorRepository.save(doctor2);

            System.out.println("🌱 Database Seeded with test doctors!");
        }
    }
}