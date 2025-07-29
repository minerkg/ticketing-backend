package org.ubb.ticketing;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.ubb.ticketing.domain.Ticket;
import org.ubb.ticketing.domain.TicketFactory;
import org.ubb.ticketing.domain.TicketType;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.dto.UserRegistrationRequest;
import org.ubb.ticketing.service.ComplaintTicketService;
import org.ubb.ticketing.service.user.TicketingUserService;

@Component
public class AddInitialData implements CommandLineRunner {

    private final ComplaintTicketService complaintTicketService;
    private final TicketingUserService ticketingUserService;
    private final Dotenv dotenv;

    public AddInitialData(ComplaintTicketService complaintTicketService, TicketingUserService ticketingUserService) {
        this.complaintTicketService = complaintTicketService;
        this.ticketingUserService = ticketingUserService;
        this.dotenv = Dotenv.load();

    }

    @Override
    public void run(String... args) throws Exception {
        Ticket complaintTicket = TicketFactory.createNewTicket(TicketType.COMPLAINT);
        complaintTicketService.save((ComplaintTicket) complaintTicket);

        String usernameAdmin = dotenv.get("USERNAME_ADMIN");
        String passwordAdmin = dotenv.get("PASSWORD_ADMIN");
        UserRegistrationRequest adminUser = UserRegistrationRequest.builder()
                .username(usernameAdmin)
                .password(passwordAdmin)
                .firstName("System")
                .lastName("Administrator")
                .email("ors@ticketing.com")
                .build();

        ticketingUserService.registerUser(adminUser);
        //ticketingUserService.updateUserRole("admin", UserRole.ADMIN);
    }
}
