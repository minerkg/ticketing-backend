package org.ubb.ticketing.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

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
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(EMAIL_USERNAME);
        mailSender.setPassword(EMAIL_PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");

        return mailSender;

    }

    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }

//    @Bean
//    public CommandLineRunner logEndpoints(RequestMappingHandlerMapping mapping) {
//        return args -> mapping.getHandlerMethods().forEach((key, value) ->
//                System.out.println("Mapped: " + key + " -> " + value)
//        );
//    }

}
