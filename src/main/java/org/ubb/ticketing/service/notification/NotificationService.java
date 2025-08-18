package org.ubb.ticketing.service.notification;

import org.ubb.ticketing.domain.Ticket;

public interface NotificationService {


        void notifyTicketCreated(String destination, String subject, String text, Ticket ticket);

        void notifyTicketAssigned(String destination, String subject, String text, Ticket ticket);



}
