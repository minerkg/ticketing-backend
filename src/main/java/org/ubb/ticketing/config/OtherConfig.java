package org.ubb.ticketing.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class OtherConfig {

    private final Dotenv dotenv = Dotenv.load();
    private final String EMAIL_USERNAME = dotenv.get("EMAIL_USERNAME");
    private final String EMAIL_PASSWORD = dotenv.get("EMAIL_PASSWORD");

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();

    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.mail.yahoo.com");
        mailSender.setPort(587);
        mailSender.setUsername(EMAIL_USERNAME);
        mailSender.setPassword(EMAIL_PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.ssl.enable", "false");
        props.put("mail.debug", "true");

        return mailSender;
    }

}
