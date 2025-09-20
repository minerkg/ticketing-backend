package org.ubb.ticketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.service.ChartDataService;

import java.util.List;


@Repository
public interface ComplaintTicketRepository extends JpaRepository<ComplaintTicket, Long> {

    @Query("SELECT t.ticketStatus AS status, COUNT(t) AS count " +
            "FROM ComplaintTicket t " +
            "WHERE t.ticketType = org.ubb.ticketing.domain.TicketType.COMPLAINT " +
            "GROUP BY t.ticketStatus")
    List<ChartDataService.StatusCount> countComplaintTicketByTicketStatus();
}
