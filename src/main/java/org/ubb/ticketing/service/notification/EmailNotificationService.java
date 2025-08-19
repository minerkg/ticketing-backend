package org.ubb.ticketing.service.notification;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.ubb.ticketing.domain.Ticket;
import org.ubb.ticketing.exception.NotificationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender javaMailSender;
    private final Dotenv dotenv = Dotenv.load();
    private final String EMAIL_FROM = dotenv.get("EMAIL_FROM");
    private final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    public EmailNotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;

    }

    @Async
    @Override
    public void notifyTicketCreated(Ticket ticket) {
        logger.debug("notifyTicketCreated method accessed");
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("date-time", ticket.getCreatedWhen().toString());
        placeholders.put("text", "Ticket created with id: " + ticket.getTicketId());
        try {
            String body = loadEmailTemplate("src/main/resources/email-templates/ticket-created.html", placeholders);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            message.setFrom(EMAIL_FROM);
            helper.setTo(ticket.getClient().getEmail());
            helper.setSubject("New ticket created");
            helper.setText(body, true);
            javaMailSender.send(message);
        } catch (IOException | MessagingException e) {
            logger.error("notifyTicketCreated internal error", e);
            throw new NotificationException(e);
        }

    }

    @Async
    @Override
    public void notifyTicketAssigned(Ticket ticket) {
        logger.debug("notifyTicketAssigned method accessed");
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("agent-name", ticket.getAssignedTo().get().getFirstName());
        placeholders.put("ticket-id", ticket.getTicketId().toString());
        placeholders.put("date-time", ticket.getCreatedWhen().toString());
        placeholders.put("ticket-summary", ticket.getDescription());


        try {
            String body = loadEmailTemplate("src/main/resources/email-templates/ticket-assigned.html", placeholders);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            message.setFrom(EMAIL_FROM);
            helper.setTo(ticket.getAssignedTo().get().getEmail());
            helper.setSubject("Ticket assigned to you");
            helper.setText(body, true);
            javaMailSender.send(message);
        } catch (IOException | MessagingException e) {
            logger.error("notifyTicketCreated internal error", e);
            throw new NotificationException(e);
        }

    }

    @Async
    @Override
    public void notifyTicketClosed(Ticket ticket) {
        logger.debug("notifyTicketClosed method accessed");
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("customer-name", ticket.getClient().getFirstName() + " " + ticket.getClient().getLastName());
        placeholders.put("ticket-id", ticket.getTicketId().toString());
        placeholders.put("date-time", ticket.getClosedWhen().toString());
        placeholders.put("resolution", ticket.getSolutionDescription());


        try {
            String body = loadEmailTemplate("src/main/resources/email-templates/ticket-closed.html", placeholders);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            message.setFrom(EMAIL_FROM);
            helper.setTo(ticket.getClient().getEmail());
            helper.setSubject("Ticket assigned to you");
            helper.setText(body, true);
            javaMailSender.send(message);
        } catch (IOException | MessagingException e) {
            logger.error("notifyTicketCreated internal error", e);
            throw new NotificationException(e);
        }

    }

    private String loadEmailTemplate(String path, Map<String, String> placeholders) throws IOException {
        String template = Files.readString(Paths.get(path));
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }

}
