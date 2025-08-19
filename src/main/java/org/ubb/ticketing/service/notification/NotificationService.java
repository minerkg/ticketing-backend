package org.ubb.ticketing.service.notification;

import org.ubb.ticketing.domain.Ticket;

public interface NotificationService {


        void notifyTicketCreated(Ticket ticket);
        void notifyTicketAssigned(Ticket ticket);
        void notifyTicketClosed(Ticket ticket);




}
