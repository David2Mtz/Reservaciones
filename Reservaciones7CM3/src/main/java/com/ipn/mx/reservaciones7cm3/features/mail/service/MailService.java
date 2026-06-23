package com.ipn.mx.reservaciones7cm3.features.mail.service;

public interface MailService {
    void sendEmail(String to, String subject, String body);
}
