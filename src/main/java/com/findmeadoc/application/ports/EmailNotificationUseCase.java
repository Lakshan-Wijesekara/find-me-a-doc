package com.findmeadoc.application.ports;

public interface EmailNotificationUseCase {
    void sendEmail(String to, String subject, String body);
}
