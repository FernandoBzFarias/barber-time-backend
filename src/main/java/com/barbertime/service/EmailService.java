package com.barbertime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public void enviarEmailRecuperacao(String destino, String token) {

        String link = "http://localhost:3000/reset-password?token=" + token;

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(destino);
        mensagem.setSubject("Recuperação de senha - BarberTime");
        mensagem.setText(
                "Clique no link para redefinir sua senha:\n\n" + link +
                "\n\nEsse link expira em 30 minutos."
        );

        mailSender.send(mensagem);
    }
}