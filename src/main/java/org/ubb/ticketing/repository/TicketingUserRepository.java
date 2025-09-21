package org.ubb.ticketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.ubb.ticketing.domain.user.TicketingUser;
import org.ubb.ticketing.dto.user.TicketingUserDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketingUserRepository extends JpaRepository<TicketingUser, Long> {

    Optional<TicketingUser> findByUsername(String username);

    Optional<TicketingUser> findByEmail(String email);

    @Query("SELECT new org.ubb.ticketing.dto.user.TicketingUserDto(u.userId, u.username, u.firstName, u.lastName, u.email, u.userRole, u.createdAt, u.accountEnabled) FROM TicketingUser u")
    List<TicketingUserDto> findAllUsersWithoutPassword();


    Optional<TicketingUser> findByUserId(UUID userId);
}
