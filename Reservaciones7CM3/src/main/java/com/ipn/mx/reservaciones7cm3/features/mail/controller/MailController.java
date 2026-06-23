package com.ipn.mx.reservaciones7cm3.features.mail.controller;

import com.ipn.mx.reservaciones7cm3.features.mail.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
@Tag(name = "Módulo de Correo Electrónico", description = "Endpoints para el envío de correos electrónicos de prueba")
public class MailController {

    private final MailService mailService;

    @PostMapping("/send-test")
    @Operation(summary = "Enviar correo de prueba", description = "Envía un correo simple utilizando el servidor SMTP configurado")
    public ResponseEntity<String> sendTestEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body) {
        mailService.sendEmail(to, subject, body);
        return ResponseEntity.ok("Correo de prueba en proceso de envío.");
    }
}
