package com.findmeadoc.application.ports;

import com.findmeadoc.application.dto.TriageResponse;

public interface AITriagePort {
    TriageResponse analyzeSymptoms(String chatId, String patientSymptoms);
}
