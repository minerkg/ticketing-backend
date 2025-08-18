package org.ubb.ticketing.service.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.ubb.ticketing.domain.Ticket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailNotificationService implements NotificationService{

    private final JavaMailSender javaMailSender;

    public EmailNotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void notifyTicketCreated(String destination, String subject, String text, Ticket ticket) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("date", ticket.getTimeSlot().getTimeInterval().getDate().toString());
        placeholders.put("time", ticket.getTimeSlot().getTimeInterval().getStartTime().toString());
        placeholders.put("text", text);

        try {
            String body = loadEmailTemplate(TemplateTYPE.RESERVATION_CREATED.getPath() /*"src/main/resources/templates/rezervation_created.html"*/, placeholders);
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            message.setFrom("bbcwasher@gmail.com");
            helper.setTo(destination);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void notifyTicketAssigned(String destination, String subject, String text, Ticket ticket) {

    }

    private String loadEmailTemplate(String path, Map<String, String> placeholders) throws IOException {
        String template = new String(
                Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8
        );

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return template;
    }

}
