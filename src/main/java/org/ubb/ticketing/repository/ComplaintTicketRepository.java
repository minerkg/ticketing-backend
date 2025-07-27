package org.ubb.ticketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;


@Repository
public interface ComplaintTicketRepository extends JpaRepository<ComplaintTicket,Long> {
}
