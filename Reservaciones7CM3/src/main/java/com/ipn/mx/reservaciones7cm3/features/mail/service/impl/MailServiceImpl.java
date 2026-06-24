package com.ipn.mx.reservaciones7cm3.features.mail.service.impl;

import com.ipn.mx.reservaciones7cm3.features.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@reservaciones.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Correo electrónico enviado con éxito a {}", to);
        } catch (Exception e) {
            log.error("Fallo al enviar correo electrónico a {}: {}", to, e.getMessage());
        }
    }
}

