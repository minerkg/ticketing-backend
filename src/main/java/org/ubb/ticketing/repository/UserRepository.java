package org.ubb.ticketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ubb.ticketing.domain.user.TicketingUser;

import java.util.Optional;

public interface UserRepository extends JpaRepository<TicketingUser, Long> {

    Optional<TicketingUser> findByUsername(String username);
    Optional<TicketingUser> findByEmail(String email);
}
