package org.ubb.ticketing.service.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.ubb.ticketing.repository.TicketingUserRepository;


@Service
public class TicketingUserDetailsService implements UserDetailsService {

    private final TicketingUserRepository ticketingUserRepository;


    public TicketingUserDetailsService(TicketingUserRepository ticketingUserRepository) {
        this.ticketingUserRepository = ticketingUserRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return ticketingUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
