package org.ubb.ticketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ubb.ticketing.domain.TicketElement;

import java.util.List;


@Repository
public interface TicketElementRepository extends JpaRepository<TicketElement, Long> {


    List<TicketElement> findByName(String name);
}
