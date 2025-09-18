package org.ubb.ticketing.service.user;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.ubb.ticketing.domain.user.TicketingUser;
import org.ubb.ticketing.repository.TicketingUserRepository;


@Service
public class TicketingUserDetailsService implements UserDetailsService {

    private final TicketingUserRepository ticketingUserRepository;


    public TicketingUserDetailsService(TicketingUserRepository ticketingUserRepository) {
        this.ticketingUserRepository = ticketingUserRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TicketingUser user = ticketingUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!user.isAccountEnabled()) {
            throw new DisabledException("Account not confirmed");
        }

        return user;
    }
}
