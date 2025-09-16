package org.ubb.ticketing.service.notification;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.ubb.ticketing.domain.Ticket;
import org.ubb.ticketing.exception.NotificationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailNotificationService implements NotificationService {

    private static final DateTimeFormatter EMAIL_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
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
        placeholders.put("customer-name", ticket.getCustomer().getFirstName() + " " + ticket.getCustomer().getLastName());
        placeholders.put("date-time", ticket.getCreatedWhen().format(EMAIL_DATE_FORMATTER));
        placeholders.put("text", "Ticket created with id: " + ticket.getTicketId());
        try {
            String body = loadEmailTemplate("ticket-created.html", placeholders);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            message.setFrom(EMAIL_FROM);
            helper.setTo(ticket.getCustomer().getEmail());
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
        placeholders.put("date-time", ticket.getCreatedWhen().format(EMAIL_DATE_FORMATTER));
        placeholders.put("ticket-summary", ticket.getDescription());


        try {
            String body = loadEmailTemplate("ticket-assigned.html", placeholders);
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
        placeholders.put("customer-name", ticket.getCustomer().getFirstName() + " " + ticket.getCustomer().getLastName());
        placeholders.put("ticket-id", ticket.getTicketId().toString());
        placeholders.put("date-time", ticket.getClosedWhen().format(EMAIL_DATE_FORMATTER));
        placeholders.put("resolution", ticket.getSolutionDescription());


        try {
            String body = loadEmailTemplate("ticket-closed.html", placeholders);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            message.setFrom(EMAIL_FROM);
            helper.setTo(ticket.getCustomer().getEmail());
            helper.setSubject("Ticket assigned to you");
            helper.setText(body, true);
            javaMailSender.send(message);
        } catch (IOException | MessagingException e) {
            logger.error("notifyTicketCreated internal error", e);
            throw new NotificationException(e);
        }

    }


    private String loadEmailTemplate(String filename, Map<String, String> placeholders) throws IOException {
        ClassPathResource resource = new ClassPathResource("email-templates/" + filename);
        try (InputStream in = resource.getInputStream()) {
            String template = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                template = template.replace("${" + entry.getKey() + "}", entry.getValue());
            }
            return template;
        }
    }


}
