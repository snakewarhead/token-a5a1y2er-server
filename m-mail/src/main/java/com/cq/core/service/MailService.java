package com.cq.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

/**
 * Created by lin on 2021-04-06.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${mail.custom.from}")
    private String from;

    public void sendMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public void sendMail(List<String> tos, String subject, String content) {
        for (String to : tos) {
            sendMail(to, subject, content);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void sendMailHtml(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom(from);
        messageHelper.setTo(subject);
        message.setSubject(subject);
        messageHelper.setText(content, true);
        mailSender.send(message);
    }

    public void sendMailAttachment(String to, String subject, String content, String filePath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        FileSystemResource file = new FileSystemResource(new File(filePath));
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
        helper.addAttachment(fileName, file);
        mailSender.send(message);
    }
}
