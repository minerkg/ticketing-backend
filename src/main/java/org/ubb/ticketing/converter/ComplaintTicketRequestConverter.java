package org.ubb.ticketing.converter;

import org.springframework.stereotype.Component;
import org.ubb.ticketing.domain.TicketFactory;
import org.ubb.ticketing.domain.TicketType;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.dto.TicketCreationRequest;
import org.ubb.ticketing.repository.TicketElementRepository;

@Component
public class ComplaintTicketRequestConverter extends BaseConverter<ComplaintTicket, TicketCreationRequest> {

    private final TicketElementRepository ticketElementRepository;
    public ComplaintTicketRequestConverter(TicketElementRepository ticketElementRepository) {
        this.ticketElementRepository = ticketElementRepository;
    }
    @Override
    ComplaintTicket convertDtoToModel(TicketCreationRequest dto) {
        var ct = TicketFactory.createNewTicket(TicketType.COMPLAINT, null);
        ct.setDescription(dto.getDescription());
        ct.setTicketElement(ticketElementRepository.findByName(dto.getTicketElementName()).getFirst());
        ct.setTicketStatus(dto.getTicketStatus());
        return (ComplaintTicket) ct;
    }

    @Override
    TicketCreationRequest convertModelToDto(ComplaintTicket entity) {
        return null;
    }
}
