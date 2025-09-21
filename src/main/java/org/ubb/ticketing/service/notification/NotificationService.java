package org.ubb.ticketing.service.notification;

import org.ubb.ticketing.domain.Ticket;
import org.ubb.ticketing.domain.user.ConfirmationToken;
import org.ubb.ticketing.domain.user.TicketingUser;

public interface NotificationService {


    void notifyTicketCreated(Ticket ticket);

    void notifyTicketAssigned(Ticket ticket);

    void notifyTicketClosed(Ticket ticket);
    void notifyTokenGenerated(TicketingUser ticketingUser, ConfirmationToken token, String baseUrl, String usecase);
}
