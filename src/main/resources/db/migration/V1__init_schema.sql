-- 1. Users Table: Handles Auth and Profiles for Admin, Doctor, and Patient
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       full_name VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(50),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Patients Table: Patient-specific details (Linked to Users)
CREATE TABLE patients (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                          age INT NOT NULL
);

-- 3. Doctors Table: Marketplace details (Linked to Users)
CREATE TABLE doctors (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                         specialization VARCHAR(100) NOT NULL,
                         consultation_fee DECIMAL(10, 2) NOT NULL,
                         is_verified BOOLEAN DEFAULT FALSE
);

-- 4. Appointments Table: The Core Transaction
CREATE TABLE appointments (
                              id BIGSERIAL PRIMARY KEY,
                              patient_id BIGINT REFERENCES patients(id) ON DELETE CASCADE, -- UPDATED: Now references patients table!
                              doctor_id BIGINT REFERENCES doctors(id) ON DELETE CASCADE,
                              appointment_date DATE NOT NULL,
                              start_time TIME NOT NULL,
                              status VARCHAR(50) NOT NULL,
                              payment_status VARCHAR(50) NOT NULL,
                              ai_brief JSONB,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Notifications Table: Admin messages and Cancellation alerts
CREATE TABLE notifications (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                               message TEXT NOT NULL,
                               is_read BOOLEAN DEFAULT FALSE,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. The Partial Unique Index to prevent double bookings
CREATE UNIQUE INDEX unique_active_booking
    ON appointments (doctor_id, appointment_date, start_time)
    WHERE status = 'BOOKED';