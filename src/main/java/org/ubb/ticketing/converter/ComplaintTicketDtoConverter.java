package org.ubb.ticketing.converter;

import org.ubb.ticketing.domain.Ticket;
import org.ubb.ticketing.domain.TicketFactory;
import org.ubb.ticketing.domain.TicketType;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.dto.TicketCreationRequest;

public class ComplaintTicketDtoConverter extends BaseConverter<ComplaintTicket, TicketCreationRequest>{
    @Override
    ComplaintTicket convertDtoToModel(TicketCreationRequest dto) {
        var ct = TicketFactory.createNewTicket(TicketType.COMPLAINT, null);
        ct.setDescription(dto.getDescription());
        ct.setTicketElement(dto.getTicketElement());
        ct.setTicketStatus(dto.getTicketStatus());
        return (ComplaintTicket) ct;
    }

    @Override
    TicketCreationRequest convertModelToDto(ComplaintTicket entity) {
        return null;
    }
}
